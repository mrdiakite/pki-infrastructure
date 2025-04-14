package com.pki.app.pki.infrastructure.service;


import com.pki.app.pki.infrastructure.model.KeyPairEntity;
import com.pki.app.pki.infrastructure.repository.KeyPairRepository;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Service
public class KeyPairService {

    @Autowired
    private KeyPairRepository keyPairRepository;

    static {
        java.security.Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Génère une paire de clés (publique et privée) et la stocke dans la base de données.
     *
     * @return L'entité KeyPairEntity sauvegardée.
     */
    public KeyPairEntity generateAndSaveKeyPair() {
        try {
            // Générer une paire de clés RSA
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Taille de la clé : 2048 bits
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Convertir les clés en format Base64 pour le stockage
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            // Créer et sauvegarder l'entité KeyPairEntity
            KeyPairEntity keyPairEntity = new KeyPairEntity();
            keyPairEntity.setPublicKey(publicKeyBase64);
            keyPairEntity.setPrivateKey(privateKeyBase64);
            return keyPairRepository.save(keyPairEntity);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating key pair: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère une paire de clés par son ID.
     *
     * @param id L'ID de la paire de clés.
     * @return L'entité KeyPairEntity correspondante.
     */
    public KeyPairEntity getKeyPairById(Long id) {
        return keyPairRepository.findById(id).orElseThrow(() -> new RuntimeException("Key pair not found"));
    }

    /**
     * Récupère une paire de clés par la clé publique.
     *
     * @param publicKey La clé publique en format Base64.
     * @return L'entité KeyPairEntity correspondante.
     */
    public KeyPairEntity getKeyPairByPublicKey(String publicKey) {
        return keyPairRepository.findByPublicKey(publicKey)
                .orElseThrow(() -> new RuntimeException("Key pair not found for the given public key"));
    }
}