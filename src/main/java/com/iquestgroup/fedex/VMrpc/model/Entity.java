package com.iquestgroup.fedex.VMrpc.model;

import java.util.List;

import lombok.Data;

@Data
public class Entity {
    
    private String name;

    private List<EntityField> fields;
}
