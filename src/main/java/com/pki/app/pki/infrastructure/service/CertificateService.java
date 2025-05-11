package com.pki.app.pki.infrastructure.service;

import com.pki.app.pki.infrastructure.model.Certificate;
import com.pki.app.pki.infrastructure.model.LdapCertificate;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class CertificateService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public List<String> getAllCertificates() {
        return ldapTemplate.search(
                "ou=certificates",                      // DN de base
                "(objectClass=inetOrgPerson)",          // Filtre LDAP
                (AttributesMapper<String>) attrs -> {
                    return (String) attrs.get("cn").get();  // Récupère le CN
                }
        );
    }

    public void generateAndStore(String commonName) throws Exception {
        if (commonName == null || commonName.trim().isEmpty()) {
            throw new IllegalArgumentException("Common name is required.");
        }

        // Génère une paire de clés
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        // Génère le certificat auto-signé
        X509Certificate cert = generateSelfSignedCert(commonName, keyPair);

        // Sauvegarde dans LDAP
        LdapCertificate ldapCert = new LdapCertificate();
        ldapCert.setCommonName(commonName);
        ldapCert.setCertificateData(cert.getEncoded());
        ldapCert.setStatus("VALID");

        ldapTemplate.create(ldapCert);


    }

    public String getCertificateByCommonName(String cn) {
        // Utilisez un filtre plus large avec des wildcards
        return ldapTemplate.search(
                "ou=certificates,dc=flash,dc=local",
                "(cn=*" + cn + "*)",  // Recherche avec wildcards
                (AttributesMapper<String>) attrs -> {
                    byte[] certBytes = (byte[]) attrs.get("userCertificate;binary").get();
                    return Base64.getEncoder().encodeToString(certBytes);
                }
        ).stream().findFirst().orElse(null);
    }

    private X509Certificate generateSelfSignedCert(String commonName, KeyPair keyPair) throws Exception {
        X500Name issuerName = new X500Name("CN=" + commonName);
        X500Name subjectName = issuerName;
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 365L * 24 * 60 * 60 * 1000);

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuerName,
                serial,
                startDate,
                endDate,
                subjectName,
                SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded())
        );

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(
                        new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate())
                ));
    }
}