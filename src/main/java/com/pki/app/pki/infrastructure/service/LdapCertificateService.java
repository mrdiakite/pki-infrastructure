package com.pki.app.pki.infrastructure.service;

import com.pki.app.pki.infrastructure.dto.CertificateData;
import com.pki.app.pki.infrastructure.model.LdapCertificate;
import com.unboundid.util.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class LdapCertificateService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private OpenSSLService openSSLService;


    private Name buildDn(String commonName) {
        return LdapNameBuilder.newInstance("dc=flash,dc=local")
                .add("ou", "certificates")
                .add("cn", commonName)
                .build();
    }

    public void generateAndStoreCertificate(String cn, String o, String ou, String l, String st, String c, String email, int validityDays) {
        try {
            // 1. Générer certificat avec OpenSSLService
            OpenSSLService.CertificateResult result = openSSLService.generateCertificate(
                    cn, o, ou, l, st, c, email, validityDays
            );

            // 2. Créer et stocker dans LdapCertificate
            LdapCertificate cert = new LdapCertificate();
            cert.setCommonName(cn);
            cert.setSerialNumber(result.serialNumber);
            cert.setCertificateData(result.certificateDer);
            cert.setStatus("Valid");
            cert.setIssuer("CN=" + cn);  // à adapter selon logique
            cert.setSubject("CN=" + cn + ", OU=" + ou);

            // 3. Enregistrer dans LDAP
            saveCertificate(cert);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération/enregistrement du certificat : " + e.getMessage(), e);
        }
    }


    public void saveCertificate(LdapCertificate cert) throws Exception {
        // 1. Validation préalable du certificat
        validateCertificate(cert.getCertificateData());

        // 2. Construction DN
        Name dn = buildDn(cert.getCommonName());

        // 3. Préparation attributs
        BasicAttributes attrs = new BasicAttributes();
        attrs.put("objectClass", "inetOrgPerson");
        attrs.put("cn", cert.getCommonName());
        attrs.put("sn", cert.getSerialNumber());

        // 4. Utilisation directe des bytes (pas besoin de ByteString)
        //attrs.put("userCertificate;binary", cert.getCertificateData());
        BasicAttribute certAttr = new BasicAttribute("userCertificate;binary");
        certAttr.add(cert.getCertificateData());
        attrs.put(certAttr);

        // 5. Enregistrement
        ldapTemplate.bind(dn, null, attrs);
    }

    private void validateCertificate(byte[] certData) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream in = new ByteArrayInputStream(certData)) {
            X509Certificate cert = (X509Certificate) cf.generateCertificate(in);

            // Validation supplémentaire
            cert.checkValidity();
            cert.verify(cert.getPublicKey()); // Auto-signature
        }
    }

    public void updateCertificate(LdapCertificate cert) {
        Name dn = buildDn(cert.getCommonName());

        BasicAttributes attrs = new BasicAttributes();
        attrs.put("sn", cert.getSerialNumber());
        attrs.put("userCertificate;binary", cert.getCertificateData());
        attrs.put("description", cert.getStatus());

        ldapTemplate.rebind(dn, null, attrs);
    }

    public void deleteCertificateByCN(String commonName) {
        Name dn = buildDn(commonName);
        ldapTemplate.unbind(dn);
    }

    public List<LdapCertificate> getAllCertificates() {
        return ldapTemplate.search(
                "ou=certificates",
                "(objectClass=inetOrgPerson)",
                (AttributesMapper<LdapCertificate>) attrs -> {
                    String commonName = (String) attrs.get("cn").get();
                    String serial = (attrs.get("sn") != null) ? (String) attrs.get("sn").get() : "";
                    String status = (attrs.get("description") != null) ? (String) attrs.get("description").get() : "";
                    String issuer = (attrs.get("ou") != null) ? (String) attrs.get("ou").get() : "";
                    String subject = "CN=" + commonName + ", OU=" + issuer;
                    byte[] certBytes = (attrs.get("userCertificate;binary") != null) ?
                            (byte[]) attrs.get("userCertificate;binary").get() : new byte[0];

                    return new LdapCertificate(commonName, serial, certBytes, subject, status, issuer);
                }
        );
    }

    public LdapCertificate getCertificateByCN(String cn) {
        return ldapTemplate.search(
                "ou=certificates",
                "(cn=" + cn + ")",
                (AttributesMapper<LdapCertificate>) attrs -> {
                    try {
                        byte[] certBytes = (byte[]) attrs.get("userCertificate;binary").get();
                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        InputStream in = new ByteArrayInputStream(certBytes);
                        X509Certificate cert = (X509Certificate) cf.generateCertificate(in);

                        LdapCertificate ldapCert = new LdapCertificate();
                        ldapCert.setSubject(cert.getSubjectX500Principal().getName());
                        ldapCert.setIssuer(cert.getIssuerX500Principal().getName());
                        ldapCert.setSerialNumber(cert.getSerialNumber().toString());
                        Date validFrom = Date.from(cert.getNotBefore().toInstant());
                        ldapCert.setValidFrom(validFrom);
                        Date validTo = Date.from(cert.getNotAfter().toInstant());
                        ldapCert.setValidTo(validTo);
                        ldapCert.setStatus("Valid");
                        ldapCert.setId(cn);
                        return ldapCert;
                    } catch (Exception e) {
                        throw new RuntimeException("Erreur lors de l’analyse du certificat LDAP : " + e.getMessage(), e);
                    }
                }
        ).stream().findFirst().orElse(null);
    }

    public byte[] getCertificateDer(X509Certificate certificate) throws CertificateEncodingException {
        return certificate.getEncoded(); // retourne un byte[] au format DER
    }



    public List<CertificateData> fetchCertificatesFromLdap() {
        return ldapTemplate.search(
                "ou=certificates",
                "(objectClass=inetOrgPerson)",
                (AttributesMapper<CertificateData>) attrs -> {
                    CertificateData data = new CertificateData();

                    String commonName = (attrs.get("cn") != null) ? (String) attrs.get("cn").get() : "";
                    String serial = (attrs.get("sn") != null) ? (String) attrs.get("sn").get() : "";
                    String status = (attrs.get("description") != null) ? (String) attrs.get("description").get() : "";
                    byte[] certBytes = (attrs.get("userCertificate;binary") != null) ?
                            (byte[]) attrs.get("userCertificate;binary").get() : new byte[0];

                    data.setSubject("CN=" + commonName); // ajuster si tu as plus d'infos
                    data.setSerialNumber(serial);
                    data.setStatus(status);

                    // Analyser le certificat pour extraire l'émetteur, validité, etc.
                    try {
                        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                        ByteArrayInputStream bis = new ByteArrayInputStream(certBytes);
                        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(bis);

                        data.setIssuer(cert.getIssuerX500Principal().getName());
                        data.setValidFrom(cert.getNotBefore().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime());
                        data.setValidTo(cert.getNotAfter().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime());
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'analyse du certificat : " + e.getMessage());
                    }

                    return data;
                }
        );
    }

}


