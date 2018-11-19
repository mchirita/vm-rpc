package com.iquestgroup.fedex.VMrpc.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class SelectedMethod implements Serializable {
    
    private String serviceMethod;
    
    private String host;
    
    private String port;
    
    private String jsonPayload;
    
    private String response;

}
