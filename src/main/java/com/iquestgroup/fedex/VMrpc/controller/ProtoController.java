package com.iquestgroup.fedex.VMrpc.controller;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iquestgroup.fedex.VMrpc.model.ProtoContent;
import com.iquestgroup.fedex.VMrpc.model.SelectedMethod;
import com.iquestgroup.fedex.VMrpc.service.GrpcService;

@Controller
public class ProtoController {
    
    @Autowired
    private GrpcService grpcService;

    @RequestMapping("/")
    public String getProto(Model model) {
        model.addAttribute("protoContent", new ProtoContent());
        return "index";
    }

    @PostMapping(value = "/protoUpload")
    public String getGrpcService(@ModelAttribute("protoContent") ProtoContent protoContent, Model model, HttpSession session) {

        Enumeration<String> definitions = null;
        List<String> definitionsList = new LinkedList<>();
        
        try {
            definitions = grpcService.generateServiceMethodsForProtoString(protoContent.getContent());
//            definitions = grpcService.generateServiceMethodsForProtoFile("helloworld.proto");

            while (definitions.hasMoreElements()) {
                definitionsList.add(definitions.nextElement());
            }
        } catch (Exception e) {
            // writing to console
            e.printStackTrace();
        }
        model.addAttribute("grpcServiceMethods", definitionsList);
        model.addAttribute("selectedMethod", new SelectedMethod());
        session.setAttribute("grpcServiceMethods", definitionsList);
        return "service";
    }
}
