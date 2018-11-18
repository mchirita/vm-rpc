package com.iquestgroup.fedex.VMrpc.util.parser;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.iquestgroup.fedex.VMrpc.model.ProtoField;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
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
    private Pattern packageName = Pattern.compile("\\spackage(.*?)\\;");
    private Pattern serviceType = Pattern.compile("\\sservice(.*?)(?<=\\{)(.+?)(?=\\})");
    private Pattern methodType = Pattern.compile("(.*?)\\s+\\((.*?)\\)\\s+returns\\s+\\((.*?)\\)");
    private Pattern messageType = Pattern.compile("\\smessage(.*?)\\s+\\{(.*?)\\}\\s+");

    public ProtoFile parseFile(String filePath) throws IOException {
        String file = loadProtoFileFromPath(filePath);
        file = formatProtoString(file);
        return ProtoFile.newBuilder()
                .packageName(extractPackageName(file))
                .services(extractServiceTypes(file))
                .messageTypes(extractMessageTypes(file)).build();
    }

    public ProtoFile parseString(String string) throws IOException {
        string = formatProtoString(string);
        return ProtoFile.newBuilder()
                .packageName(extractPackageName(string))
                .services(extractServiceTypes(string))
                .messageTypes(extractMessageTypes(string)).build();
    }

    private String loadProtoFileFromPath(String path) throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        return CharStreams.toString(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private String formatProtoString(String string) {
        string = string.replace("\n", " ").trim();
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


    private List<ProtoMessageType> extractMessageTypes(String file) {
        List<ProtoMessageType> messageTypes = Lists.newArrayList();
        Matcher m = messageType.matcher(file);
        while (m.find()) {
            //for (int i = 1; i <= m.groupCount(); i++) {
            String messageTypeName = m.group(1).trim();
            String messageTypeContent = m.group(2).trim();

            messageTypes.add(ProtoMessageType.newBuilder()
                    .name(messageTypeName)
                    .fields(extractFieldTypes(messageTypeContent)).build());
            // }
        }
        if (messageTypes.isEmpty()) {
            throw new IllegalArgumentException("No message type define in proto file: " + file);
        }
        return messageTypes;
    }

    private List<ProtoField> extractFieldTypes(String messageTypeContent) {
        List<ProtoField> fields = Lists.newArrayList();
        String[] fieldsAsString = messageTypeContent.split(";");
        for (String field : fieldsAsString) {
            ProtoField.Builder protoFieldBuilder = ProtoField.newBuilder();

            field = updateBuilderWithFieldLabelIfPresent(protoFieldBuilder, field);
            field = updateBuilderWithFieldType(protoFieldBuilder, field);
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
     * At this point the field contains e.g. "string name = 1"
     *
     * @returns e.g. "name = 1"
     */
    private String updateBuilderWithFieldType(ProtoField.Builder protoFieldBuilder, String field) {
        for (Map.Entry<String, Type> entry : com.iquestgroup.fedex.VMrpc.util.FieldMappings.SIGNATURE_TYPES.entrySet()) {
            String type = entry.getKey();
            if (field.contains(type)) {
                protoFieldBuilder.fieldType(entry.getValue());
                return field.replaceFirst(type, "").trim();
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

    public static void main(String[] args) throws IOException {
        String file = "syntax = \"proto3\";\n" +
                "\n" +
                "option java_multiple_files = true;\n" +
                "option java_package = \"io.grpc.examples.helloworld\";\n" +
                "option java_outer_classname = \"HelloWorldProto\";\n" +
                "option objc_class_prefix = \"HLW\";\n" +
                "\n" +
                "package helloworld;\n" +
                "\n" +
                "message HelloRequest {\n" +
                "    string name = 1;\n" +
                "}\n" +
                "\n" +
                "message HelloReply {\n" +
                "    string message = 1;\n" +
                "}\n" +
                "\n" +
                "service Greeter {\n" +
                "    rpc SayHello (HelloRequest) returns (HelloReply) {}\n" +
                "}\n";

        ProtoFile protoFile = new ProtoParser().parseString(file);

       System.out.println(protoFile);
    }
}
