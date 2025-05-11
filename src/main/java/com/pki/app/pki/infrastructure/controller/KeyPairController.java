package com.pki.app.pki.infrastructure.controller;

import com.pki.app.pki.infrastructure.model.LdapCertificate;
import com.pki.app.pki.infrastructure.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.KeyPair;
import java.util.Base64;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.Name;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;

@Controller
public class KeyPairController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private LdapTemplate ldapTemplate;

    @GetMapping("/keypair")
    public String keyPairHome(Model model) {
        return "keypairs"; // Retourne le template keypairs.html
    }

    /**
     * Affiche le formulaire pour générer une paire de clés.
     */
    @GetMapping("/generate-keypair")
    public String showGenerateKeyPairForm() {
        return "generate-keypair";
    }

    /**
     * Génère une paire de clés et les stocke dans LDAP.
     */
    @PostMapping("/generate-keypair")
    public String generateKeyPair(
            @RequestParam(required = false, defaultValue = "2048") int keySize,
            Model model) {

        try {
            // Générer la paire de clés
            KeyPair keyPair = encryptionService.generateAsymmetricKeyPair(keySize);

            // Encoder les clés
            byte[] publicKey = keyPair.getPublic().getEncoded();
            byte[] privateKey = keyPair.getPrivate().getEncoded();

            // Construire le DN
            Name dn = LdapNameBuilder.newInstance("dc=flash,dc=local")
                    .add("ou", "keys")
                    .add("cn", "asymmetric-keypair")
                    .build();

            // Créer les attributs
            BasicAttributes attrs = new BasicAttributes();
            attrs.put("objectClass", "inetOrgPerson");
            attrs.put("cn", "asymmetric-keypair");
            attrs.put("sn", "keypair001");
            attrs.put(new BasicAttribute("userCertificate;binary", publicKey));
            attrs.put(new BasicAttribute("description", Base64.getEncoder().encodeToString(privateKey)));

            // Stocker dans LDAP
            ldapTemplate.bind(dn, null, attrs);

            model.addAttribute("message", "Paire de clés générée et stockée dans LDAP avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la génération ou du stockage de la paire de clés : " + e.getMessage());
        }

        return "generate-keypair";
    }

    /**
     * Affiche la paire de clés stockée dans LDAP.
     */
    @GetMapping("/view-keypair")
    public String viewKeyPair(Model model) {
        try {
            Name dn = LdapNameBuilder.newInstance("dc=flash,dc=local")
                    .add("ou", "keys")
                    .add("cn", "asymmetric-keypair")
                    .build();

            javax.naming.directory.Attributes attrs = ldapTemplate.lookup(dn, (AttributesMapper<Attributes>) context -> context);


            byte[] publicKey = (byte[]) attrs.get("userCertificate;binary").get();
            String privateKey = (String) attrs.get("description").get();

            model.addAttribute("publicKey", Base64.getEncoder().encodeToString(publicKey));
            model.addAttribute("privateKey", privateKey);

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération de la paire de clés : " + e.getMessage());
        }

        return "keypairs";
    }
}
