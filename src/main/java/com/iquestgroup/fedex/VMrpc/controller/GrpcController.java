package com.iquestgroup.fedex.VMrpc.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.SessionScope;

import com.iquestgroup.fedex.VMrpc.model.SelectedMethod;
import com.iquestgroup.fedex.VMrpc.service.GrpcService;

@Controller
@SessionScope
public class GrpcController {
    
    @Autowired
    private GrpcService grpcService;
    
    @GetMapping("/service")
    public String getService(Model model) {
        return "service";
    }

    @PostMapping(value = "/selectMethod")
    public String runServiceMethod(Model model, @ModelAttribute("selectedMethod") SelectedMethod selectedMethod, HttpSession session) {
        
        try {
            String response = grpcService.call(selectedMethod.getHost(), Integer.parseInt(selectedMethod.getPort()), selectedMethod.getServiceMethod(), selectedMethod.getJsonPayload());
            selectedMethod.setResponse(response);
        } catch (Exception e) {
            selectedMethod.setResponse("Exception occured: " + e.toString());
        }
        model.addAttribute("grpcServiceMethods", session.getAttribute("grpcServiceMethods"));
        model.addAttribute("serviceMethod", selectedMethod);

        return "service";
    }
}
