package com.iquestgroup.fedex.VMrpc.model;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", builderMethodName = "newBuilder")
public class ProtoField {
    private Label fieldLabel;
    private Type fieldType;
    private String fieldTypeName;
    private String fieldName;
    private int position = 1;
}
