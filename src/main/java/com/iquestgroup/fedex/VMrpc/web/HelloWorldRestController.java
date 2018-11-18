package com.iquestgroup.fedex.VMrpc.web;

import com.iquestgroup.fedex.VMrpc.service.GrpcService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;


@RestController
public class HelloWorldRestController {
    private static Logger logger = LoggerFactory.getLogger(HelloWorldRestController.class);

    @Autowired
    GrpcService grpcService;

    @RequestMapping("/")
    public Enumeration<String> generateServiceMethodsForProtoFile() throws Exception {
        return grpcService.generateServiceMethodsForProtoFile("helloworld.proto");
    }

    @RequestMapping("/call/{methodName}")
    public String message(@PathVariable String methodName) {

        try {
            return grpcService.call("localhost", 50051, "helloworld.Greeter/SayHello",
                    "{ \"name\" : \"Mr. Alan Turing\" }");
        } catch (Exception e) {
            logger.error("Error: ", e);
            return ExceptionUtils.getStackTrace(e);
        }
    }
}
