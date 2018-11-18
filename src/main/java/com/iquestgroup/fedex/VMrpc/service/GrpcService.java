package com.iquestgroup.fedex.VMrpc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.google.common.net.HostAndPort;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
import com.iquestgroup.fedex.VMrpc.model.ProtoMethod;
import com.iquestgroup.fedex.VMrpc.service.grpc.ChannelFactory;
import com.iquestgroup.fedex.VMrpc.service.grpc.GeneralBlockingStub;
import com.iquestgroup.fedex.VMrpc.util.generator.MethodDescriptorGenerator;
import com.iquestgroup.fedex.VMrpc.util.generator.ProtoDynamicSchemaGenerator;
import com.iquestgroup.fedex.VMrpc.util.parser.ProtoParser;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GrpcService {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ProtoParser protoParser;

    @Autowired
    private ProtoDynamicSchemaGenerator schemaGenerator;

    @Autowired
    private MethodDescriptorGenerator methodDescriptorGenerator;

    @Autowired
    private ChannelFactory channelFactory;

    private ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, MethodDescriptor> descriptorMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<MethodDescriptor, ProtoMethod> methodMap = new ConcurrentHashMap<>();
    private DynamicSchema schema;

    public Enumeration<String> generateServiceMethodsForProtoFile(String filePath) throws Exception {

        ProtoFile protoFile = protoParser.parseFile(filePath);

        methodMap.putAll(methodDescriptorGenerator.generateMethodDescriptors(protoFile));
        for (MethodDescriptor methodDescriptor : methodMap.keySet()) {
            descriptorMap.put(methodDescriptor.getFullMethodName(), methodDescriptor);
        }

        schema = schemaGenerator.generateSchemaFromFile(protoFile);
        return descriptorMap.keys();
    }

    public Enumeration<String> generateServiceMethodsForProtoString(String protoFileContent) throws Exception {

        ProtoFile protoFile = protoParser.parseString(protoFileContent);

        methodMap.putAll(methodDescriptorGenerator.generateMethodDescriptors(protoFile));
        for (MethodDescriptor methodDescriptor : methodMap.keySet()) {
            descriptorMap.put(methodDescriptor.getFullMethodName(), methodDescriptor);
        }

        schema = schemaGenerator.generateSchemaFromFile(protoFile);
        return descriptorMap.keys();
    }


    public String call(String hostname, int port, String serviceMethod, String jsonPayload) throws Exception {
        Channel channel = channelMap.computeIfAbsent(hostname + ":" + port, k -> channelFactory.createChannel(HostAndPort.fromParts(hostname, port)));
        MethodDescriptor methodDescriptor = descriptorMap.get(serviceMethod);
        ProtoMethod method = methodMap.get(methodDescriptor);
        DynamicMessage msg = createMessageUsingSchemaAndPayload(schema, jsonPayload, method);

        GeneralBlockingStub blockingStub = new GeneralBlockingStub(channel);
        return blockingStub.sendMessage(msg, methodDescriptor).toString();
    }

    private DynamicMessage createMessageUsingSchemaAndPayload(DynamicSchema schema, String jsonPayload, ProtoMethod serviceMethod) throws IOException {
        DynamicMessage.Builder msgBuilder = schema.newMessageBuilder(serviceMethod.getInputType());
        Descriptors.Descriptor msgDesc = msgBuilder.getDescriptorForType();

        Map<String, Object> jsonMap = mapper.readValue(jsonPayload, new TypeReference<Map<String, Object>>() {
        });
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            Descriptors.FieldDescriptor fieldDescriptor = msgDesc.findFieldByName(entry.getKey());
            if(fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
                Descriptors.EnumValueDescriptor enumValueDescriptorProto = fieldDescriptor.getEnumType().findValueByName(entry.getValue().toString());
                msgBuilder.setField(fieldDescriptor, enumValueDescriptorProto.getNumber());
            } else {
                msgBuilder.setField(fieldDescriptor, entry.getValue());
            }
        }

        return msgBuilder.build();
    }
}
