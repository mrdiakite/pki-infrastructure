package com.pki.app.pki.infrastructure.repository;

import com.pki.app.pki.infrastructure.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    // Retourne un Optional<Certificate> pour gérer le cas où aucun certificat n'est trouvé
    Optional<Certificate> findByCertificateData(String certificateData);
}