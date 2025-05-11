package com.pki.app.pki.infrastructure.controller;


import com.pki.app.pki.infrastructure.service.HashAndSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HashingController {

    @Autowired
    private HashAndSignatureService hashAndSignatureService;

    @GetMapping("/hash")
    public String hashForm() {
        return "hash";
    }

    @PostMapping("/hash")
    public String hash(@RequestParam String input, Model model) {
        try {
            String hashedValue = hashAndSignatureService.hash(input);
            model.addAttribute("hashedValue", hashedValue);
        } catch (Exception e) {
            model.addAttribute("error", "Error during hashing: " + e.getMessage());
        }
        return "hash";
    }
}