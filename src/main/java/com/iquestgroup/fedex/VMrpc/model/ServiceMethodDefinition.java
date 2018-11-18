package com.iquestgroup.fedex.VMrpc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceMethodDefinition {

    private String serviceName;

    private String methodName;

    private String requestType;

    private String responseType;
}
