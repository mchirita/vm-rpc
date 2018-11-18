package com.iquestgroup.fedex.VMrpc.util.converter;

import com.google.common.collect.Lists;
import com.google.protobuf.DescriptorProtos;
import com.iquestgroup.fedex.VMrpc.model.ProtoField;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
import com.iquestgroup.fedex.VMrpc.model.ProtoMessageType;
import com.iquestgroup.fedex.VMrpc.model.ProtoMethod;
import com.iquestgroup.fedex.VMrpc.model.ProtoService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProtoFileToDescriptorProtoConverter {

    public DescriptorProtos.FileDescriptorProto convertFromProtoFile(ProtoFile protoFile) {
        DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto
                .newBuilder()
                .setName(protoFile.getPackageName());

        builder.addAllService(convertToProtoServices(protoFile));
        builder.addAllMessageType(convertFromProtoMessageTypes(protoFile.getMessageTypes()));

        return builder.build();
    }

    private List<DescriptorProtos.ServiceDescriptorProto> convertToProtoServices(ProtoFile protoFile) {
        List<DescriptorProtos.ServiceDescriptorProto> serviceDescriptorProtos = Lists.newArrayList();
        for (ProtoService service : protoFile.getServices()) {
            serviceDescriptorProtos.add(DescriptorProtos.ServiceDescriptorProto.newBuilder()
                    .setName(service.getName())
                    .addAllMethod(convertFromProtoMethods(service.getMethods()))
                    .build());
        }
        return serviceDescriptorProtos;
    }

    private List<DescriptorProtos.DescriptorProto> convertFromProtoMessageTypes(List<ProtoMessageType> messageTypes) {
        List<DescriptorProtos.DescriptorProto> descriptorProtos = Lists.newArrayList();
        for (ProtoMessageType messageType : messageTypes) {
            descriptorProtos.add(DescriptorProtos.DescriptorProto.newBuilder()
                    .setName(messageType.getName())
                    .addAllField(convertFromProtoFields(messageType))
                    .build());
        }
        return descriptorProtos;
    }

    private List<DescriptorProtos.FieldDescriptorProto> convertFromProtoFields(ProtoMessageType type) {
        List<DescriptorProtos.FieldDescriptorProto> fieldDescriptorProtos = Lists.newArrayList();
        for (ProtoField field : type.getFields()) {
            DescriptorProtos.FieldDescriptorProto.Builder fieldDescriptorBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder();

            fieldDescriptorBuilder
                    .setName(field.getFieldName())
                    .setType(field.getFieldType())
                    .setNumber(field.getPosition());
            if (field.getFieldLabel() != null) {
                fieldDescriptorBuilder
                        .setLabel(field.getFieldLabel());
            }
            fieldDescriptorProtos.add(fieldDescriptorBuilder.build());
        }
        return fieldDescriptorProtos;
    }

    private List<DescriptorProtos.MethodDescriptorProto> convertFromProtoMethods(List<ProtoMethod> methods) {
        List<DescriptorProtos.MethodDescriptorProto> methodDescriptorProtos = Lists.newArrayList();
        for (ProtoMethod protoMethod : methods) {
            methodDescriptorProtos.add(DescriptorProtos.
                    MethodDescriptorProto.newBuilder()
                    .setName(protoMethod.getName())
                    .setInputType(protoMethod.getInputType())
                    .setOutputType(protoMethod.getOutputType())
                    .build());
        }
        return methodDescriptorProtos;
    }
}
