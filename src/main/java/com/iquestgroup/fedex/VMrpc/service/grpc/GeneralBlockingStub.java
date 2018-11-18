package com.iquestgroup.fedex.VMrpc.service.grpc;

import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;

public final class GeneralBlockingStub extends io.grpc.stub.AbstractStub<GeneralBlockingStub> {

    public GeneralBlockingStub(io.grpc.Channel channel) {
        super(channel);
    }

    private GeneralBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
        super(channel, callOptions);
    }

    @java.lang.Override
    protected GeneralBlockingStub build(io.grpc.Channel channel,
                                        io.grpc.CallOptions callOptions) {
        return new GeneralBlockingStub(channel, callOptions);
    }

    public Object sendMessage(Object request, MethodDescriptor methodDescriptor) {
        return ClientCalls.blockingUnaryCall(
                getChannel(), methodDescriptor, getCallOptions(), request);
    }

}
