package com.pki.app.pki.infrastructure.controller;

import com.pki.app.pki.infrastructure.dto.CertificateDTO;
import com.pki.app.pki.infrastructure.service.LdapCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateRestController {

    private final LdapCertificateService ldapCertificateService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateAndStoreCert(@RequestBody CertificateDTO req) {
        try {
            ldapCertificateService.generateAndStoreCertificate(
                    req.getCn(), req.getO(), req.getOu(), req.getL(), req.getSt(), req.getC(),
                    req.getEmail(), req.getValidityDays()
            );
            return ResponseEntity.ok("Certificat généré et stocké avec succès dans LDAP.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("Erreur : " + e.getMessage());
        }
    }
}
