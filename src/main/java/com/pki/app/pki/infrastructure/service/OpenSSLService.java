package com.pki.app.pki.infrastructure.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

@Service
public class OpenSSLService {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static class CertificateResult {
        public final String serialNumber;
        private final String certificatePem;
        public final byte[] certificateDer;
        public byte[] privateKeyDer;

        public CertificateResult(String serialNumber, String certificatePem, byte[] certificateDer) {
            this.serialNumber = serialNumber;
            this.certificatePem = certificatePem;
            this.certificateDer = certificateDer;
        }

        // Getters
        public String getSerialNumber() {
            return serialNumber;
        }

        public String getCertificatePem() {
            return certificatePem;
        }

        public byte[] getCertificateDer() {
            return certificateDer;
        }
    }


    public CertificateResult generateCertificate(
            String commonName, String organization, String organizationalUnit,
            String locality, String state, String country, String email,
            int validityDays) throws Exception {

        // 1. Génération de la paire de clés
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // 2. Construction du sujet
        String subjectDN = String.format(
                "CN=%s, O=%s, OU=%s, L=%s, ST=%s, C=%s, EMAILADDRESS=%s",
                commonName, organization, organizationalUnit,
                locality, state, country, email
        );
        X500Name subject = new X500Name(subjectDN);

        // 3. Paramètres de validité
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + validityDays * 24L * 60 * 60 * 1000);
        BigInteger serial = new BigInteger(64, new SecureRandom());

        // 4. Construction du certificat
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                subject, serial, notBefore, notAfter, subject,
                SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded())
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(keyPair.getPrivate());

        X509CertificateHolder certHolder = certBuilder.build(signer);

        // Ajoutez BasicConstraints pour certificat CA
        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(true));

        // 5. Conversion en formats divers
        // Format DER (pour LDAP)
        byte[] derCert = certHolder.getEncoded();

        // Format PEM (pour affichage/export)
        StringWriter pemWriter = new StringWriter();
        try (PEMWriter pemW = new PEMWriter(pemWriter)) {
            pemW.writeObject(certHolder);
        }
        String pemCert = pemWriter.toString();

        // Format Java X509Certificate
        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certHolder);

        return new CertificateResult(
                serial.toString(),  // Numéro de série
                pemCert,           // Certificat au format PEM
                derCert           // Certificat au format DER
        );
    }

    public byte[] convertPemToDer(String pemContent) throws Exception {
        try (InputStream in = new ByteArrayInputStream(pemContent.getBytes(StandardCharsets.UTF_8))) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
            return cert.getEncoded(); // <-- DER binaire
        }
    }

    //------##------
    //public byte[] convertPemToDer(String pemData) throws Exception {
        //PEMParser parser = new PEMParser(new StringReader(pemData));
        //Object obj = parser.readObject();

        //if (obj instanceof X509CertificateHolder) {
            //return ((X509CertificateHolder) obj).getEncoded();
        //}
        //throw new IllegalArgumentException("Le PEM ne contient pas un certificat X.509 valide");
    //}


    public CertificateResult generateKeyPairAndCertificate(
            String commonName, String organization, String organizationalUnit,
            String locality, String state, String country, String email,
            int validityDays
    ) throws Exception {
        return generateCertificate(commonName, organization, organizationalUnit,
                locality, state, country, email, validityDays);
    }

    public String readCertificate(String certPath) throws Exception {
        return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(certPath)));
    }

    public byte[] readCertificateBytes(String certPath) throws Exception {
        return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(certPath));
    }

}
