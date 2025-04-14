package com.pki.app.pki.infrastructure.model;

public class LdapCertificate {

    private String cn;
    private String serialNumber;
    private byte[] certificate;
    private String issuer;

    public LdapCertificate() {}

    public LdapCertificate(String cn, String serialNumber, byte[] certificate, String issuer) {
        this.cn = cn;
        this.serialNumber = serialNumber;
        this.certificate = certificate;
        this.issuer = issuer;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
