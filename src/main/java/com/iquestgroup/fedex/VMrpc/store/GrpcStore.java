package com.iquestgroup.fedex.VMrpc.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.iquestgroup.fedex.VMrpc.model.ServiceMethod;

@Service
public class GrpcStore {

    private Map<String, List<ServiceMethod>> uploadedGrpcMap = new HashMap<>();
    
    public void addGrpc(String protoKey, List<ServiceMethod> serviceMethodList) {
        uploadedGrpcMap.put(protoKey, serviceMethodList);
    }
    
//    public List<ServiceMethod>
    
}
