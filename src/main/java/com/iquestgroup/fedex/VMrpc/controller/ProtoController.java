package com.iquestgroup.fedex.VMrpc.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import com.iquestgroup.fedex.VMrpc.model.ProtoContent;
import com.iquestgroup.fedex.VMrpc.model.SelectedMethod;
import com.iquestgroup.fedex.VMrpc.model.ServiceMethod;
import com.iquestgroup.fedex.VMrpc.model.ServiceMethodDefinition;
import com.iquestgroup.fedex.VMrpc.parser.ProtoContentParser;

@Controller
@SessionScope
public class ProtoController {
    
    @Autowired
    private ProtoContentParser contentParser;

    @RequestMapping("/proto")
    public String getProto(Model model) {
        model.addAttribute("protoContent", new ProtoContent());
        return "index";
    }

    @PostMapping(value = "/protoUpload")
    public String getGrpcService(@ModelAttribute("protoContent") ProtoContent protoContent, Model model, HttpSession session) {

        List<ServiceMethodDefinition> definitions = contentParser.parse(protoContent.getContent());
        
        List<ServiceMethod> grpcServiceMethods = new LinkedList<>();
        ServiceMethod method = new ServiceMethod();
        method.setName("method name 1");
        grpcServiceMethods.add(method);
        ServiceMethod method2 = new ServiceMethod();
        method2.setName("method name 2");
        grpcServiceMethods.add(method2);
        model.addAttribute("grpcServiceMethods", definitions);
        model.addAttribute("selectedMethod", new SelectedMethod());
        return "service";
    }
}
