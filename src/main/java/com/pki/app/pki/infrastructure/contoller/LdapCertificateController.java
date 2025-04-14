package com.pki.app.pki.infrastructure.contoller;

import com.pki.app.pki.infrastructure.dto.CertificateDTO;
import com.pki.app.pki.infrastructure.service.LdapCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/certificate")
public class LdapCertificateController {

    @Autowired
    private LdapCertificateService ldapService;

    @GetMapping("/view-certificates-from-ldap")
    public String viewCertificatesFromLdap(Model model) {
        try {
            // Récupérer les certificats depuis OpenLDAP
            List<CertificateDTO> certificates = ldapService.fetchCertificatesFromLdap();

            // Ajouter les certificats au modèle Thymeleaf
            model.addAttribute("certificates", certificates);
            model.addAttribute("message", "Certificates fetched successfully from OpenLDAP.");
        } catch (Exception e) {
            // En cas d'erreur, ajouter un message d'erreur
            model.addAttribute("error", "Failed to fetch certificates from OpenLDAP: " + e.getMessage());
        }

        return "ldap"; // Nom du fichier HTML (sans extension)
    }
}