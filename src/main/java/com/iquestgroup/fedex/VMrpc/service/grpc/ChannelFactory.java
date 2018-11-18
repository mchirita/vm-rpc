package com.iquestgroup.fedex.VMrpc.service.grpc;
import com.google.common.net.HostAndPort;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.grpc.StatusException;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class ChannelFactory {

    public Channel createChannel(HostAndPort endpoint) {
        NettyChannelBuilder nettyChannelBuilder = createChannelBuilder(endpoint);

        return nettyChannelBuilder.build();
    }

    private NettyChannelBuilder createChannelBuilder(HostAndPort endpoint) {
            return NettyChannelBuilder.forAddress(endpoint.getHost(), endpoint.getPort())
                    .negotiationType(NegotiationType.PLAINTEXT);

    }

    private ClientInterceptor metadataInterceptor() {
        ClientInterceptor interceptor = new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    final io.grpc.MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, final Channel next) {
                return new ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                    @Override
                    protected void checkedStart(Listener<RespT> responseListener, Metadata headers)
                            throws StatusException {
                        delegate().start(responseListener, headers);
                    }
                };
            }
        };

        return interceptor;
    }
}