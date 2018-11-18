package com.iquestgroup.fedex.VMrpc.web;

import com.iquestgroup.fedex.VMrpc.service.GrpcService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloWorldRestController {
    private static Logger logger = LoggerFactory.getLogger(HelloWorldRestController.class);

    @Autowired
    GrpcService grpcService;

    @RequestMapping("/")
    public String welcome() {
        return "Welcome to GRPC Example.";
    }

    @RequestMapping("/hello")
    public String message() {

        try {
            return grpcService.call();
        } catch (Exception e) {
            logger.error("Error: ", e);
            return ExceptionUtils.getStackTrace(e);
        }
        // return "all good";
    }
}
