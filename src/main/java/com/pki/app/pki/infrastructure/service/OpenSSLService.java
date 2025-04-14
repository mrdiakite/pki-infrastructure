package com.pki.app.pki.infrastructure.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class OpenSSLService {

    /**
     * Génère une paire de clés et un certificat avec les informations fournies.
     *
     * @param commonName          Nom commun (CN)
     * @param organization        Organisation (O)
     * @param organizationalUnit  Unité organisationnelle (OU)
     * @param locality            Localité (L)
     * @param state               État ou province (ST)
     * @param country             Pays (C)
     * @param email               Adresse email
     * @param validityDays        Durée de validité en jours
     * @param outputPath          Chemin de sortie pour les fichiers générés
     * @throws IOException        En cas d'erreur d'entrée/sortie
     * @throws InterruptedException En cas d'interruption du processus
     */
    public void generateKeyPairAndCertificate(
            String commonName, String organization, String organizationalUnit,
            String locality, String state, String country, String email,
            int validityDays, String outputPath) throws IOException, InterruptedException {

        // Générer une clé privée
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("openssl", "genpkey", "-algorithm", "RSA", "-out", outputPath + "/private_key.pem");
        Process process = processBuilder.start();
        process.waitFor();

        // Générer une demande de signature de certificat (CSR)
        String subject = String.format(
                "/CN=%s/O=%s/OU=%s/L=%s/ST=%s/C=%s/emailAddress=%s",
                commonName, organization, organizationalUnit, locality, state, country, email
        );
        processBuilder.command(
                "openssl", "req", "-new", "-key", outputPath + "/private_key.pem",
                "-out", outputPath + "/csr.pem", "-subj", subject
        );
        process = processBuilder.start();
        process.waitFor();

        // Générer un certificat auto-signé
        processBuilder.command(
                "openssl", "x509", "-req", "-in", outputPath + "/csr.pem",
                "-signkey", outputPath + "/private_key.pem", "-out", outputPath + "/certificate.pem",
                "-days", String.valueOf(validityDays)
        );
        process = processBuilder.start();
        process.waitFor();
    }

    /**
     * Lit le certificat généré à partir du fichier.
     *
     * @param outputPath Chemin du fichier de certificat
     * @return Le contenu du certificat
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public String readCertificate(String outputPath) throws IOException {
        StringBuilder certificate = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputPath + "/certificate.pem")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                certificate.append(line).append("\n");
            }
        }
        return certificate.toString();
    }
}