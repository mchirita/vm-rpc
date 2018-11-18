package com.iquestgroup.fedex.VMrpc.util.generator;

import com.google.common.collect.Maps;
import com.iquestgroup.fedex.VMrpc.model.ProtoFile;
import com.iquestgroup.fedex.VMrpc.model.ProtoMethod;
import com.iquestgroup.fedex.VMrpc.model.ProtoService;
import com.iquestgroup.fedex.VMrpc.service.grpc.DynamicMessageMarshaller;
import io.grpc.MethodDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.grpc.MethodDescriptor.generateFullMethodName;

@Component
public class MethodDescriptorGenerator {

    public Map<MethodDescriptor, ProtoMethod> generateMethodDescriptors(ProtoFile protoFile) {
        Map<MethodDescriptor, ProtoMethod> methodMap = Maps.newHashMap();
        for (ProtoService service : protoFile.getServices()) {
            for (ProtoMethod method : service.getMethods()) {
                MethodDescriptor methodDescriptor = createMethodDescriptor(protoFile.getPackageName(), service.getName(), method.getName());
                methodMap.put(methodDescriptor, method);
            }
        }
        return methodMap;
    }

    private MethodDescriptor createMethodDescriptor(String packageName, String serviceName, String methodName) {

        return MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(generateFullMethodName(
                        packageName + "." + serviceName, methodName))
                .setResponseMarshaller(DynamicMessageMarshaller.INSTANCE)
                .setRequestMarshaller(DynamicMessageMarshaller.INSTANCE)
                .build();
    }
}
