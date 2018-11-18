package com.iquestgroup.fedex.VMrpc.util.parser;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.iquestgroup.fedex.VMrpc.model.ProtoField;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
import com.iquestgroup.fedex.VMrpc.model.ProtoEnumType;
import com.iquestgroup.fedex.VMrpc.model.ProtoMessageType;
import com.iquestgroup.fedex.VMrpc.model.ProtoMethod;
import com.iquestgroup.fedex.VMrpc.model.ProtoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ProtoParser {
    private static final String ENUM_TYPE_REGEX = "\\senum(.*?)\\s+\\{(.*?)\\}\\s+";
    private Pattern packageName = Pattern.compile("\\spackage(.*?)\\;");
    private Pattern serviceType = Pattern.compile("\\sservice(.*?)(?<=\\{)(.+?)(?=\\})");
    private Pattern methodType = Pattern.compile("(.*?)\\s+\\((.*?)\\)\\s+returns\\s+\\((.*?)\\)");
    private Pattern messageType = Pattern.compile("\\s?message(.*?)\\s+\\{(.*?)\\}\\s?");
    private Pattern enumType = Pattern.compile(ENUM_TYPE_REGEX);

    public ProtoFile parseFile(String filePath) throws IOException {
        String file = loadProtoFileFromPath(filePath);
        file = formatProtoString(file);
        List<ProtoEnumType> enumTypes = extractEnumTypes(file);
        return ProtoFile.newBuilder()
                .packageName(extractPackageName(file))
                .services(extractServiceTypes(file))
                .enumTypes(enumTypes)
                .messageTypes(extractMessageTypes(enumTypes, file.replaceAll(ENUM_TYPE_REGEX, " ")))
                .build();
    }

    public ProtoFile parseString(String string) throws IOException {
        string = formatProtoString(string);
        List<ProtoEnumType> enumTypes = extractEnumTypes(string);
        return ProtoFile.newBuilder()
                .packageName(extractPackageName(string))
                .services(extractServiceTypes(string))
                .enumTypes(enumTypes)
                .messageTypes(extractMessageTypes(enumTypes, string.replaceAll(ENUM_TYPE_REGEX, " ")))
                .build();
    }

    private String loadProtoFileFromPath(String path) throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        return CharStreams.toString(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private String formatProtoString(String string) {
        string = string.replace("\n", " ").trim();
        string = string.replace("\r", " ").trim();
        string = string.replace("\t", " ").trim();
        string = string.replaceAll(" +", " ");
        string = string.replace("{ }", " ").trim();
        string = string.replace("{}", " ").trim();
        string = string.replaceAll(" +", " ");
        return string;
    }

    private String extractPackageName(String file) {
        Matcher m = packageName.matcher(file);
        while (m.find()) {
            return m.group(1).trim();
        }
        throw new IllegalArgumentException("No package name define in proto file: " + file);
    }

    private List<ProtoService> extractServiceTypes(String file) {
        List<ProtoService> services = Lists.newArrayList();
        Matcher m = serviceType.matcher(file);//"service Greeter { rpc SayHello (HelloRequest) returns (HelloReply) rpc SayHello (HelloRequest) returns (HelloReply) }"
        while (m.find()) {
            String serviceName = m.group(1).replace("{", "").trim(); // Greeter
            String methodsContent = m.group(2).trim();// //"rpc SayHello (HelloRequest) returns (HelloReply) rpc SayHello (HelloRequest) returns (HelloReply) "
            String[] methods = methodsContent.split("rpc");// "", "SayHello (HelloRequest) returns (HelloReply)", "SayHello (HelloRequest) returns (HelloReply) "
            services.add(ProtoService.newBuilder()
                    .name(serviceName)
                    .methods(extractMethodsForService(methods))
                    .build());
        }
        return services;
    }

    private List<ProtoMethod> extractMethodsForService(String[] methods) {
        List<ProtoMethod> methodList = Lists.newArrayList();
        for (String method : methods) {
            if (StringUtils.isNotBlank(method)) {
                Matcher m = methodType.matcher(method);
                while (m.find()) {
                    methodList.add(ProtoMethod.newBuilder()
                            .name(m.group(1).trim())
                            .inputType(m.group(2).trim())
                            .outputType(m.group(3).trim())
                            .build()
                    );
                }
            }
        }
        return methodList;
    }


    private List<ProtoMessageType> extractMessageTypes(List<ProtoEnumType> enumTypes, String file) {
        List<ProtoMessageType> messageTypes = Lists.newArrayList();
        Matcher m = messageType.matcher(file);
        while (m.find()) {

            String messageTypeName = m.group(1).trim();
            String messageTypeContent = m.group(2).trim();

            messageTypes.add(ProtoMessageType.newBuilder()
                    .name(messageTypeName)
                    .fields(extractFieldTypes(enumTypes, messageTypeContent)).build());

        }
        if (messageTypes.isEmpty()) {
            throw new IllegalArgumentException("No message type is defined in the proto file: " + file);
        }
        return messageTypes;
    }

    private List<ProtoEnumType> extractEnumTypes(String file) {
        List<ProtoEnumType> enumTypes = Lists.newArrayList();
        Matcher m = enumType.matcher(file);
        while (m.find()) {

            String enumTypeName = m.group(1).trim();
            String enumTypeContent = m.group(2).trim();

            enumTypes.add(ProtoEnumType.newBuilder()
                    .name(enumTypeName)
                    .fields(extractEnumFieldTypes(enumTypeContent)).build());

        }
        return enumTypes;
    }

    private List<ProtoField> extractEnumFieldTypes(String enumTypeContent) {
        List<ProtoField> fields = Lists.newArrayList();
        String[] fieldsAsString = enumTypeContent.split(";");
        for (String field : fieldsAsString) {
            ProtoField.Builder protoFieldBuilder = ProtoField.newBuilder();
            field = updateBuilderWithFieldNameAndPosition(protoFieldBuilder, field);
            fields.add(protoFieldBuilder.build());
        }
        return fields;
    }


    private List<ProtoField> extractFieldTypes(List<ProtoEnumType> enumTypes, String messageTypeContent) {
        List<ProtoField> fields = Lists.newArrayList();
        String[] fieldsAsString = messageTypeContent.split(";");
        for (String field : fieldsAsString) {
            ProtoField.Builder protoFieldBuilder = ProtoField.newBuilder();

            field = updateBuilderWithFieldLabelIfPresent(protoFieldBuilder, field);
            field = updateBuilderWithFieldType(enumTypes, protoFieldBuilder, field);
            field = updateBuilderWithFieldNameAndPosition(protoFieldBuilder, field);

            fields.add(protoFieldBuilder.build());
        }
        return fields;
    }

    /**
     * At this point the field contains e.g.
     * "repeated string name = 1"
     * "string name = 1"
     *
     * @returns e.g. "string name = 1"
     */
    private String updateBuilderWithFieldLabelIfPresent(ProtoField.Builder protoFieldBuilder, String field) {
        for (Map.Entry<String, Label> entry : com.iquestgroup.fedex.VMrpc.util.FieldMappings.LABEL_TYPES.entrySet()) {
            String label = entry.getKey();
            if (field.contains(label)) {
                protoFieldBuilder.fieldLabel(entry.getValue());
                return field.replaceFirst(label, "").trim();
            }
        }
        return field;
    }

    /**
     * At this point the field contains e.g. "string name = 1", "TestEnum name = 1"
     *
     * @returns e.g. "name = 1"
     */
    private String updateBuilderWithFieldType(List<ProtoEnumType> enumTypes, ProtoField.Builder protoFieldBuilder, String field) {
        for (Map.Entry<String, Type> entry : com.iquestgroup.fedex.VMrpc.util.FieldMappings.SIGNATURE_TYPES.entrySet()) {
            String type = entry.getKey();
            if (field.contains(type)) {
                protoFieldBuilder.fieldType(entry.getValue());
                return field.replaceFirst(type, "").trim();
            }
        }
        for (ProtoEnumType enumType: enumTypes) {
            if (field.contains(enumType.getName())) {
                protoFieldBuilder.fieldType(Type.TYPE_ENUM);
                protoFieldBuilder.fieldTypeName(enumType.getName());
                return field.replaceFirst(enumType.getName(), "").trim();
            }
        }
        return field;
    }


    /**
     * At this point the field contains e.g. "name = 1"
     *
     * @returns e.g. ""
     */
    private String updateBuilderWithFieldNameAndPosition(ProtoField.Builder protoFieldBuilder, String field) {
        String[] strings = field.split("=");
        if (strings.length != 2) {
            throw new IllegalArgumentException("Couldn't split by '=' in order to retrieve name and postion from field: " + field);
        }
        protoFieldBuilder.fieldName(strings[0].trim());
        protoFieldBuilder.position(Integer.valueOf(strings[1].trim()));
        return field;
    }
}
