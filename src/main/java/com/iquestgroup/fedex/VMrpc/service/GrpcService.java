package com.iquestgroup.fedex.VMrpc.service;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.google.common.net.HostAndPort;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.iquestgroup.fedex.VMrpc.util.generator.ProtoDynamicSchemaGenerator;
import com.iquestgroup.fedex.VMrpc.service.grpc.ChannelFactory;
import com.iquestgroup.fedex.VMrpc.service.grpc.DynamicMessageMarshaller;
import com.iquestgroup.fedex.VMrpc.service.grpc.GeneralBlockingStub;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.grpc.MethodDescriptor.generateFullMethodName;

@Service
public class GrpcService {

    @Autowired
    private ProtoDynamicSchemaGenerator schemaGenerator;

    @Autowired
    private ChannelFactory channelFactory;

    public String call() throws Exception {
        DynamicSchema schema = schemaGenerator.generateSchemaFromFile("helloworld.proto");
        DynamicMessage msg = createSimpleDynamicMessageForTest(schema);

        Channel channel = channelFactory.createChannel(HostAndPort.fromParts("localhost", 50051));

        MethodDescriptor methodDescriptor = createMethodDescriptor();
        GeneralBlockingStub greeterBlockingStub = new GeneralBlockingStub(channel);
        return greeterBlockingStub.sendMessage(msg, methodDescriptor).toString();
    }

    private DynamicMessage createSimpleDynamicMessageForTest(DynamicSchema schema) {
        DynamicMessage.Builder msgBuilder = schema.newMessageBuilder("HelloRequest");
        Descriptors.Descriptor msgDesc = msgBuilder.getDescriptorForType();

        return msgBuilder
                .setField(msgDesc.findFieldByName("name"), "Alan Turing")
                .build();
    }

    private MethodDescriptor createMethodDescriptor() {

        return MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(generateFullMethodName(
                        "helloworld.Greeter", "SayHello"))
                .setResponseMarshaller(DynamicMessageMarshaller.INSTANCE)
                .setRequestMarshaller(DynamicMessageMarshaller.INSTANCE)
                .build();
    }

}
