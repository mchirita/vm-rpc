package com.iquestgroup.fedex.VMrpc.model;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "Builder", builderMethodName = "newBuilder")
public class ProtoFile {

    private String packageName;
    private List<ProtoService> services = Lists.newArrayList();
    private List<ProtoMessageType> messageTypes = Lists.newArrayList();
    private List<ProtoEnumType> enumTypes = Lists.newArrayList();
 }
