package com.iquestgroup.fedex.VMrpc.model;

import lombok.Data;

@Data
public class ServiceMethod {

    private String name;
    
    private Entity requestEntity;
    
    private Entity responseEntity;
}
