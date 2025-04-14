package com.pki.app.pki.infrastructure.service;

import com.pki.app.pki.infrastructure.dto.CertificateDTO;
import com.pki.app.pki.infrastructure.model.LdapCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

@Service
public class LdapCertificateService {

    @Autowired
    private LdapTemplate ldapTemplate;

    private Name buildDn(String cn) {
        return LdapNameBuilder.newInstance("dc=example,dc=com")
                .add("ou", "certificates")
                .add("cn", cn)
                .build();
    }

    public void saveCertificate(LdapCertificate cert) {
        Name dn = buildDn(cert.getCn());

        BasicAttributes attrs = new BasicAttributes();
        attrs.put("objectClass", "inetOrgPerson");
        attrs.put("cn", cert.getCn());
        attrs.put("sn", cert.getSerialNumber());
        attrs.put("userCertificate;binary", cert.getCertificate());
        attrs.put("description", cert.getIssuer());

        ldapTemplate.bind(dn, null, attrs);
    }

    public void updateCertificate(LdapCertificate cert) {
        Name dn = buildDn(cert.getCn());

        BasicAttributes attrs = new BasicAttributes();
        attrs.put("sn", cert.getSerialNumber());
        attrs.put("userCertificate;binary", cert.getCertificate());
        attrs.put("description", cert.getIssuer());

        ldapTemplate.rebind(dn, null, attrs);
    }

    public void deleteCertificateByCN(String cn) {
        Name dn = buildDn(cn);
        ldapTemplate.unbind(dn);
    }

    public List<LdapCertificate> getAllCertificates() {
        return ldapTemplate.search(
                "ou=certificates",
                "(objectClass=inetOrgPerson)",
                (AttributesMapper<LdapCertificate>) attrs -> {
                    String cn = (String) attrs.get("cn").get();
                    String sn = (String) attrs.get("sn").get();
                    String description = (attrs.get("description") != null) ? (String) attrs.get("description").get() : "";
                    byte[] certBytes = (attrs.get("userCertificate;binary") != null) ?
                            (byte[]) attrs.get("userCertificate;binary").get() : new byte[0];

                    return new LdapCertificate(cn, sn, certBytes, description);
                }
        );
    }

    public List<CertificateDTO> fetchCertificatesFromLdap() {
        // Simuler la récupération de certificats depuis OpenLDAP
        List<CertificateDTO> certificates = new ArrayList<>();

        CertificateDTO cert1 = new CertificateDTO();
        cert1.setSerialNumber("123456789");
        cert1.setSubject("CN=Test User, O=Test Org, C=FR");
        cert1.setIssuer("CN=Test CA, O=Test Org, C=FR");
        cert1.setValidFrom(java.time.LocalDateTime.now());
        cert1.setValidTo(java.time.LocalDateTime.now().plusYears(1));
        cert1.setStatus("VALID");

        certificates.add(cert1);

        return certificates;

    }
}

