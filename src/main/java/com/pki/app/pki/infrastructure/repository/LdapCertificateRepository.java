package com.pki.app.pki.infrastructure.repository;

import com.pki.app.pki.infrastructure.model.LdapCertificate;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
import java.util.Date;
import java.util.List;

@Repository
public interface LdapCertificateRepository extends LdapRepository<LdapCertificate> {

    // Trouver par nom commun (cn)
    LdapCertificate findByCommonName(String commonName);

    // Trouver tous les certificats valides
    List<LdapCertificate> findByStatus(String status);

    // Trouver par DN (Distinguished Name)
    LdapCertificate findByDn(Name dn);

    // Méthode personnalisée pour trouver des certificats expirant avant une date
    // (Nécessite une implémentation personnalisée - voir ci-dessous)
    List<LdapCertificate> findByExpirationDateBefore(Date date);
}