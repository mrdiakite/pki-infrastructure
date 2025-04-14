package com.pki.app.pki.infrastructure.service;


import com.pki.app.pki.infrastructure.model.Certificate;
import com.pki.app.pki.infrastructure.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * Sauvegarde un certificat dans la base de données.
     *
     * @param certificateData Le contenu du certificat (en base64 ou texte).
     * @return L'entité Certificate sauvegardée.
     */
    public Certificate saveCertificate(String certificateData) {
        Certificate certificate = new Certificate();
        certificate.setCertificateData(certificateData);
        return certificateRepository.save(certificate);
    }

    /**
     * Récupère un certificat par son ID.
     *
     * @param id L'ID du certificat.
     * @return L'entité Certificate correspondante.
     */
    public Certificate getCertificateById(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    /**
     * Récupère un certificat par son contenu.
     *
     * @param certificateData Le contenu du certificat (en base64 ou texte).
     * @return L'entité Certificate correspondante.
     */
    public Certificate getCertificateByData(String certificateData) {
        return certificateRepository.findByCertificateData(certificateData)
                .orElseThrow(() -> new RuntimeException("Certificate not found for the given data"));
    }

    /**
     * Récupère tous les certificats stockés dans la base de données.
     *
     * @return Une liste de tous les certificats.
     */
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }
}