package com.pki.app.pki.infrastructure.controller;


import com.pki.app.pki.infrastructure.service.LdapHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatusController {
    @Autowired
    private LdapHealthService healthService;

    @GetMapping("/status")
    public String getStatus(Model model) {
        model.addAttribute("ldapStatus", healthService.checkLdapStatus());
        return "status-page";
    }
}

