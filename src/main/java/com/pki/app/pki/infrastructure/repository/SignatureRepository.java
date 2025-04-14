package com.pki.app.pki.infrastructure.repository;


import com.pki.app.pki.infrastructure.model.SignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureRepository extends JpaRepository<SignatureEntity, Long> {
    // Méthodes personnalisées si nécessaire
    // Exemple : Trouver une signature par les données signées
    SignatureEntity findByData(String data);
}