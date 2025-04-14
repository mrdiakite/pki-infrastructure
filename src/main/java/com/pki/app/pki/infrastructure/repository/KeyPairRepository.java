package com.pki.app.pki.infrastructure.repository;

import com.pki.app.pki.infrastructure.model.KeyPairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyPairRepository extends JpaRepository<KeyPairEntity, Long> {
    // Retourne un Optional<KeyPairEntity> pour gérer le cas où aucune paire de clés n'est trouvée
    Optional<KeyPairEntity> findByPublicKey(String publicKey);
}