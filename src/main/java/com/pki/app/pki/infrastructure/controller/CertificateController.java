package com.pki.app.pki.infrastructure.controller;

import com.pki.app.pki.infrastructure.dto.CertificateData;
import com.pki.app.pki.infrastructure.model.LdapCertificate;
import com.pki.app.pki.infrastructure.service.CertificateService;
import com.pki.app.pki.infrastructure.service.LdapCertificateService;
import com.pki.app.pki.infrastructure.service.OpenSSLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    private OpenSSLService openSSLService;

    @Autowired
    private LdapCertificateService ldapcertificateService;

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/certificate")
    public String showCertificates(Model model) {
        model.addAttribute("certList", certificateService.getAllCertificates());
        return "certificate"; // le nom de ton template Thymeleaf
    }

    @GetMapping("")
    public String certificateHome(Model model) {
        try {
            List<LdapCertificate> certificates = ldapcertificateService.getAllCertificates();
            model.addAttribute("certificates", ldapcertificateService.fetchCertificatesFromLdap());

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des certificats : " + e.getMessage());
        }
        return "certificate"; // Assurez-vous que ce template existe
    }

    @GetMapping("/generate-certificate")
    public String showForm(Model model) {
        model.addAttribute("certificate", new CertificateData());
        return "generate-certificate";
    }

    @PostMapping("/generate-certificate")
    public String generate(
            @ModelAttribute("certificate") CertificateData certificate,
            Model model,
            RedirectAttributes redirectAttributes) throws Exception {


            // 1. Génération du certificat
            OpenSSLService.CertificateResult result = openSSLService.generateKeyPairAndCertificate(
                    certificate.getCommonName(),
                    certificate.getOrganization(),
                    certificate.getOrganizationalUnit(),
                    certificate.getLocality(),
                    certificate.getState(),
                    certificate.getCountry(),
                    certificate.getEmail(),
                    certificate.getValidityDays()
            );

            // 2. Préparation pour LDAP
            byte[] certBytes = result.getCertificateDer();
            LdapCertificate ldapCert = new LdapCertificate();
            ldapCert.setCommonName(certificate.getCommonName());
            ldapCert.setSerialNumber(result.getSerialNumber());
            //ldapCert.setCertificateData(result.getCertificateDer());
            ldapCert.setCertificateData(certBytes);
            ldapCert.setStatus("Valid");
            ldapCert.setIssuer("CN=" + certificate.getCommonName()); // à ajuster si besoin
            ldapCert.setSubject("CN=" + certificate.getCommonName() + ", OU=" + certificate.getOrganizationalUnit());


            // Conversion PEM vers DER si nécessaire
            byte[] certDer = openSSLService.convertPemToDer(result.getCertificatePem());
            ldapCert.setCertificateData(certDer);


            // 3. Sauvegarde
            ldapcertificateService.saveCertificate(ldapCert);

            redirectAttributes.addFlashAttribute("message", "Certificat généré et stocké avec succès !");

            return "redirect:/certificate/generate-certificate";


    }

    @PostMapping("/store")
    public String storeCertificate(@RequestParam("cn") String cn,
                                   @RequestParam("serial") String serial,
                                   @RequestParam("issuer") String issuer,
                                   @RequestParam("status") String status,
                                   @RequestParam("subject") String subject,
                                   @RequestParam("cert") MultipartFile certFile,
                                   Model model) {
        try {
            byte[] content = certFile.getBytes();
            LdapCertificate cert = new LdapCertificate(cn, serial, content, issuer, status, subject);
            ldapcertificateService.saveCertificate(cert);
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
            List<LdapCertificate> certificates = ldapcertificateService.getAllCertificates();
            model.addAttribute("certificates", certificates);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des certificats : " + e.getMessage());
        }
        return "certificate";
    }

    @PostMapping("/delete")
    public String deleteCertificate(@RequestParam("cn") String cn, Model model) {
        try {
            ldapcertificateService.deleteCertificateByCN(cn);
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
                                    @RequestParam("status") String status,
                                    @RequestParam("subject") String subject,
                                    @RequestParam("cert") MultipartFile certFile,
                                    Model model) {
        try {
            byte[] content = certFile.getBytes();
            LdapCertificate updatedCert = new LdapCertificate(cn, serial, content, issuer, status, subject);
            ldapcertificateService.updateCertificate(updatedCert);
            model.addAttribute("message", "Certificat mis à jour avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la mise à jour : " + e.getMessage());
        }
        return "redirect:/certificate";
    }

    @GetMapping("/ldap/{cn}")
    public String viewLdapCertificate(@PathVariable String cn, Model model) {
        try {
            LdapCertificate cert = ldapcertificateService.getCertificateByCN(cn);
            if (cert == null) {
                model.addAttribute("error", "Certificat introuvable pour CN=" + cn);
            } else {
                model.addAttribute("certificates", List.of(cert)); // La vue attend une liste
            }
            return "ldap";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération du certificat : " + e.getMessage());
            return "ldap";
        }
    }




}
