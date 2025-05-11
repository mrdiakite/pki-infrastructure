package com.pki.app.pki.infrastructure.controller;

import com.pki.app.pki.infrastructure.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Controller
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @GetMapping("/encrypt-symmetric")
    public String encryptSymmetricForm() {
        return "encrypt-symmetric";
    }

    @PostMapping("/encrypt-symmetric")
    public String encryptSymmetric(@RequestParam String plainText, Model model) {
        try {
            SecretKey secretKey = encryptionService.generateSymmetricKey();
            String encryptedText = encryptionService.encryptSymmetric(plainText, secretKey);
            model.addAttribute("encryptedText", encryptedText);
        } catch (Exception e) {
            model.addAttribute("error", "Error during symmetric encryption: " + e.getMessage());
        }
        return "encrypt-symmetric";
    }

    @GetMapping("/decrypt-symmetric")
    public String decryptSymmetricForm() {
        return "decrypt-symmetric";
    }

    @PostMapping("/decrypt-symmetric")
    public String decryptSymmetric(@RequestParam String encryptedText, @RequestParam String key, Model model) {
        try {
            SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
            String decryptedText = encryptionService.decryptSymmetric(encryptedText, secretKey);
            model.addAttribute("decryptedText", decryptedText);
        } catch (Exception e) {
            model.addAttribute("error", "Error during symmetric decryption: " + e.getMessage());
        }
        return "decrypt-symmetric";
    }

    @GetMapping("/encrypt-asymmetric")
    public String encryptAsymmetricForm() {
        return "encrypt-asymmetric";
    }

    @PostMapping("/encrypt-asymmetric")
    public String encryptAsymmetric(@RequestParam String plainText, @RequestParam String publicKey, Model model) {
        try {
            PublicKey key = encryptionService.getPublicKeyFromString(publicKey);
            String encryptedText = encryptionService.encryptAsymmetric(plainText, key);
            model.addAttribute("encryptedText", encryptedText);
        } catch (Exception e) {
            model.addAttribute("error", "Error during asymmetric encryption: " + e.getMessage());
        }
        return "encrypt-asymmetric";
    }

    @GetMapping("/decrypt-asymmetric")
    public String decryptAsymmetricForm() {
        return "decrypt-asymmetric";
    }

    @PostMapping("/decrypt-asymmetric")
    public String decryptAsymmetric(@RequestParam String encryptedText, @RequestParam String privateKey, Model model) {
        try {
            PrivateKey key = encryptionService.getPrivateKeyFromString(privateKey);
            String decryptedText = encryptionService.decryptAsymmetric(encryptedText, key);
            model.addAttribute("decryptedText", decryptedText);
        } catch (Exception e) {
            model.addAttribute("error", "Error during asymmetric decryption: " + e.getMessage());
        }
        return "decrypt-asymmetric";
    }
}