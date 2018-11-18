package com.iquestgroup.fedex.VMrpc.parser;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.iquestgroup.fedex.VMrpc.model.ServiceMethodDefinition;

@Component
public class ProtoContentParser {

    public List<ServiceMethodDefinition> parse(String content) {
        List<ServiceMethodDefinition> serviceMethodDefinitions = new LinkedList<>();

        String[] services = content.split("service");
        if (services != null && services.length > 1)
            for (int i = 1; i < services.length; i++) {
                try {
                    String serviceName = services[i].substring(0, services[i].indexOf("{"))
                            .trim();
                    String[] methods = services[i].substring(services[i].indexOf("{"))
                            .split("rpc");
                    if (methods != null && methods.length > 1) {
                        for (int j = 1; j < methods.length; j++) {
                            int endIndex = methods[j].indexOf("(");
                            if (endIndex == -1)
                                continue;
                            String methodName = methods[j].substring(0, methods[j].indexOf("("))
                                    .trim();
                            String[] types = methods[j].split("\\(");
                            String requestType = types[1].substring(0, types[1].indexOf(")"))
                                    .trim();
                            String responseType = types[2].substring(0, types[2].indexOf(")"))
                                    .trim();
                            serviceMethodDefinitions.add(ServiceMethodDefinition.builder()
                                    .serviceName(serviceName)
                                    .methodName(methodName)
                                    .requestType(requestType)
                                    .responseType(responseType)
                                    .build());
                        }
                    }
                } catch (Throwable t) {
                       // continue and do noting
                    // cannot parse entry - problem in proto file
                }
            }

        return serviceMethodDefinitions;

    }

}
