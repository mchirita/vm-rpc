package com.iquestgroup.fedex.VMrpc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", builderMethodName = "newBuilder")
public class ProtoMethod {

    private String name;
    private String inputType;
    private String outputType;
}
