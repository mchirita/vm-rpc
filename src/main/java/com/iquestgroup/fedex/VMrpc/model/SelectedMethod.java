package com.iquestgroup.fedex.VMrpc.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class SelectedMethod implements Serializable {
    
    private String serviceMethod;

}
