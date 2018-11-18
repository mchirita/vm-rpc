package com.iquestgroup.fedex.VMrpc.util.generator;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.EnumDefinition;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.DescriptorProtos;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
import com.iquestgroup.fedex.VMrpc.util.FieldMappings;
import com.iquestgroup.fedex.VMrpc.util.converter.ProtoFileToDescriptorProtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProtoDynamicSchemaGenerator {

    @Autowired
    private ProtoFileToDescriptorProtoConverter descriptorProtoConverter;

    public DynamicSchema generateSchemaFromFile(ProtoFile protoFile) throws Exception {

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
                if(field.getType() == DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM) {
                    msgDefBuilder.addEnumDefinition( addEnumDefinitionForField(field, proto));
                }
                msgDefBuilder.addField(
                        FieldMappings.getKeyFromLabel(field.getLabel()),
                        retrieveKey(proto, field),
                        field.getName(),
                        field.getNumber());
            }
            schemaBuilder.addMessageDefinition(msgDefBuilder.build());
        }

        return schemaBuilder.build();
    }

    private EnumDefinition addEnumDefinitionForField(DescriptorProtos.FieldDescriptorProto field, DescriptorProtos.FileDescriptorProto proto) {
        EnumDefinition.Builder builder = EnumDefinition.newBuilder(field.getTypeName());
        for(DescriptorProtos.EnumDescriptorProto descriptorProto : proto.getEnumTypeList()) {
            if(descriptorProto.getName().equals(field.getTypeName())) {
                for (DescriptorProtos.EnumValueDescriptorProto enumValueDescriptorProto : descriptorProto.getValueList()){
                    builder.addValue(enumValueDescriptorProto.getName(), enumValueDescriptorProto.getNumber());
                }
                return builder.build();
            }
        }
        return null;
    }

    private String retrieveKey(DescriptorProtos.FileDescriptorProto proto, DescriptorProtos.FieldDescriptorProto field) {
        if(field.getType() != DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM) {
            return FieldMappings.getKeyFromType(field.getType());
        }

            for(DescriptorProtos.EnumDescriptorProto descriptorProto : proto.getEnumTypeList()) {
                if(descriptorProto.getName().equals(field.getTypeName())){
                    return descriptorProto.getName();
                }
            }
        throw new IllegalArgumentException("No key for type: " + field.getType());
    }
}
