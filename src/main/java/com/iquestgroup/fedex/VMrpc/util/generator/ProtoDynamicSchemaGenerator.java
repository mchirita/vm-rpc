package com.iquestgroup.fedex.VMrpc.util.generator;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.DescriptorProtos;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
import com.iquestgroup.fedex.VMrpc.util.converter.ProtoFileToDescriptorProtoConverter;
import com.iquestgroup.fedex.VMrpc.util.parser.ProtoParser;
import com.iquestgroup.fedex.VMrpc.util.FieldMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProtoDynamicSchemaGenerator {

    @Autowired
    private ProtoParser protoParser;

    @Autowired
    private ProtoFileToDescriptorProtoConverter descriptorProtoConverter;

    public DynamicSchema generateSchemaFromFile(String filePath) throws Exception {
        ProtoFile protoFile = protoParser.parseFile(filePath);
        DescriptorProtos.FileDescriptorProto proto = descriptorProtoConverter.convertFromProtoFile(protoFile);

        return createDynamicSchemaUsingProtoDescriptor(proto);
    }

    private DynamicSchema createDynamicSchemaUsingProtoDescriptor(DescriptorProtos.FileDescriptorProto proto) throws Exception {
        DynamicSchema.Builder schemaBuilder = DynamicSchema.newBuilder()
                .setName(proto.getName())
                .setPackage(proto.getPackage());


        for (DescriptorProtos.DescriptorProto messageType : proto.getMessageTypeList()) {
            MessageDefinition.Builder msgDefBuilder = MessageDefinition.newBuilder(messageType.getName());

            for (DescriptorProtos.FieldDescriptorProto field : messageType.getFieldList()) {
                msgDefBuilder.addField(
                        FieldMappings.getKeyFroLabel(field.getLabel()),
                        FieldMappings.getKeyFroType(field.getType()),
                                field.getName(),
                                field.getNumber());
            }
            schemaBuilder.addMessageDefinition(msgDefBuilder.build());
        }

        return schemaBuilder.build();
    }
}
