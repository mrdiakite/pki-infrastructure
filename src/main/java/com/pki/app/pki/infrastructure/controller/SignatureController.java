package com.pki.app.pki.infrastructure.controller;


import com.pki.app.pki.infrastructure.service.EncryptionService;
import com.pki.app.pki.infrastructure.service.HashAndSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.PrivateKey;
import java.security.PublicKey;

@Controller
public class SignatureController {

    @Autowired
    private HashAndSignatureService hashAndSignatureService;

    @Autowired
    private EncryptionService encryptionService;

    @GetMapping("/sign")
    public String signForm() {
        return "sign";
    }

    @PostMapping("/sign")
    public String sign(@RequestParam String data, @RequestParam String privateKey, Model model) {
        try {
            PrivateKey key = encryptionService.getPrivateKeyFromString(privateKey);
            String signature = hashAndSignatureService.sign(data, key);
            model.addAttribute("signature", signature);
        } catch (Exception e) {
            model.addAttribute("error", "Error during signing: " + e.getMessage());
        }
        return "sign";
    }

    @GetMapping("/verify")
    public String verifyForm() {
        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String data, @RequestParam String signature, @RequestParam String publicKey, Model model) {
        try {
            PublicKey key = encryptionService.getPublicKeyFromString(publicKey);
            boolean isValid = hashAndSignatureService.verify(data, signature, key);
            model.addAttribute("isValid", isValid);
        } catch (Exception e) {
            model.addAttribute("error", "Error during verification: " + e.getMessage());
        }
        return "verify";
    }
}