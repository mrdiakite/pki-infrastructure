package com.pki.app.pki.infrastructure.contoller;

import com.pki.app.pki.infrastructure.model.LdapCertificate;
import com.pki.app.pki.infrastructure.service.LdapCertificateService;
import com.pki.app.pki.infrastructure.service.OpenSSLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    private OpenSSLService openSSLService;

    @Autowired
    private LdapCertificateService certificateService;

    @GetMapping("/generate-certificate")
    public String showGenerateCertificateForm() {
        return "generate-certificate";
    }

    @PostMapping("/generate-certificate")
    public String generateCertificate(
            @RequestParam String commonName,
            @RequestParam String organization,
            @RequestParam String organizationalUnit,
            @RequestParam String locality,
            @RequestParam String state,
            @RequestParam String country,
            @RequestParam String email,
            @RequestParam int validityDays,
            @RequestParam String outputPath,
            Model model) {

        try {
            openSSLService.generateKeyPairAndCertificate(
                    commonName, organization, organizationalUnit, locality, state, country, email, validityDays, outputPath
            );
            String certificate = openSSLService.readCertificate(outputPath);
            model.addAttribute("message", "Certificat généré avec succès !");
            model.addAttribute("certificate", certificate);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la génération du certificat : " + e.getMessage());
            e.printStackTrace();
        }

        return "generate-certificate";
    }

    @PostMapping("/store")
    public String storeCertificate(@RequestParam("cn") String cn,
                                   @RequestParam("serial") String serial,
                                   @RequestParam("issuer") String issuer,
                                   @RequestParam("cert") MultipartFile certFile,
                                   Model model) {
        try {
            byte[] content = certFile.getBytes();
            LdapCertificate cert = new LdapCertificate(cn, serial, content, issuer);
            certificateService.saveCertificate(cert);
            model.addAttribute("message", "Certificat stocké avec succès dans OpenLDAP !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du stockage dans OpenLDAP : " + e.getMessage());
            e.printStackTrace();
        }

        return "generate-certificate";
    }

    @GetMapping("/list")
    public String listCertificates(Model model) {
        try {
            List<LdapCertificate> certificates = certificateService.getAllCertificates();
            model.addAttribute("certificates", certificates);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des certificats : " + e.getMessage());
        }
        return "certificate";
    }

    @PostMapping("/delete")
    public String deleteCertificate(@RequestParam("cn") String cn, Model model) {
        try {
            certificateService.deleteCertificateByCN(cn);
            model.addAttribute("message", "Certificat supprimé avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/certificate";
    }

    @PostMapping("/update")
    public String updateCertificate(@RequestParam("cn") String cn,
                                    @RequestParam("serial") String serial,
                                    @RequestParam("issuer") String issuer,
                                    @RequestParam("cert") MultipartFile certFile,
                                    Model model) {
        try {
            byte[] content = certFile.getBytes();
            LdapCertificate updatedCert = new LdapCertificate(cn, serial, content, issuer);
            certificateService.updateCertificate(updatedCert);
            model.addAttribute("message", "Certificat mis à jour avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la mise à jour : " + e.getMessage());
        }
        return "redirect:/certificate";
    }

    @GetMapping("/view-certificate")
    public String viewCertificate(Model model) {
        model.addAttribute("info", "Fonction non encore implémentée : récupération depuis OpenLDAP.");
        return "view-certificate";
    }
}
