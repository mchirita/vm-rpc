package com.iquestgroup.fedex.VMrpc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import com.iquestgroup.fedex.VMrpc.model.SelectedMethod;
import com.iquestgroup.fedex.VMrpc.model.ServiceMethod;

@Controller
@SessionScope
public class GrpcController {
    
    @GetMapping("/service")
    public String getService(Model model) {
        return "service";
    }
    
    @GetMapping("/method")
    public String getMethod(Model model) {
        return "method";
    }

    @PostMapping(value = "/selectMethod")
    public String runServiceMethod(Model model, @ModelAttribute("selectedMethod") SelectedMethod selectedMethod) {
        model.addAttribute("serviceMethod", selectedMethod.getClass());

        return "redirect:method";
    }
}
