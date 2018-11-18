package com.iquestgroup.fedex.VMrpc.model;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "Builder", builderMethodName = "newBuilder")
public class ProtoEnumType {
    private String name;
    private List<ProtoField> fields = Lists.newArrayList();
}
