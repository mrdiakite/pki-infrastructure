package com.pki.app.pki.infrastructure.dto;

import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

public class CertificateData {


    private String subject;
    private String serialNumber;
    private String status;
    private String issuer;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    String commonName;
    String organization;
    String organizationalUnit;
    String locality;
    String state;
    String country;
    String email;
    int validityDays;

    public CertificateData() {
    }

    public CertificateData(String commonName, String organization, String organizationalUnit, String locality, String state, String country, String email, int validityDays) {
        this.commonName = commonName;
        this.organization = organization;
        this.organizationalUnit = organizationalUnit;
        this.locality = locality;
        this.state = state;
        this.country = country;
        this.email = email;
        this.validityDays = validityDays;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter et Setter pour Subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter et Setter pour SerialNumber
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    // Getter et Setter pour Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter et Setter pour Issuer
    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    // Getter et Setter pour ValidFrom
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    // Getter et Setter pour ValidTo
    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(int validityDays) {
        this.validityDays = validityDays;
    }


    @Override
    public String toString() {
        return "CertificateData{" +
                "commonName='" + commonName + '\'' +
                ", organization='" + organization + '\'' +
                ", organizationalUnit='" + organizationalUnit + '\'' +
                ", locality='" + locality + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", email='" + email + '\'' +
                ", validityDays=" + validityDays +
                '}';
    }
}
