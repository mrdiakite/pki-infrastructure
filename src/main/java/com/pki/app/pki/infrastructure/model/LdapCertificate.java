package com.pki.app.pki.infrastructure.model;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.time.ZoneId;
import java.util.Date;
import java.util.Arrays;

@Entry(objectClasses = {"inetOrgPerson", "top"})
public class LdapCertificate {

    @Id
    private Name dn;
    @Attribute(name = "cn")
    private String commonName;
    @Attribute(name = "userCertificate;binary")
    private byte[] certificateData;
    @Attribute(name = "description")
    private String status;
    @Attribute(name = "serialNumber")
    private String serialNumber;
    private Date validFrom;
    private Date validTo;
    private String issuer;
    private String subject;
    private String id;

    // Facultatif si ton schéma le permet
    @Attribute(name = "validUntil")
    private Date expirationDate;

    public LdapCertificate() {}

    public LdapCertificate(String commonName, String serialNumber, byte[] certificateData, String status, String subject, String issuer) {
        this.commonName = commonName;
        this.serialNumber = serialNumber;
        this.certificateData = certificateData;
        this.status = status;
        this.issuer = issuer;
    }

    public Name getDn() {
        return dn;
    }

    public void setDn(Name dn) {
        this.dn = dn;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public byte[] getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(byte[] certificateData) {
        this.certificateData = certificateData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // toString (facultatif)
    @Override
    public String toString() {
        return "LdapCertificate{" +
                "commonName='" + commonName + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", subject='" + subject + '\'' +
                ", issuer='" + issuer + '\'' +
                ", status='" + status + '\'' +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", id='" + id + '\'' +
                ", certBytes=" + Arrays.toString(certificateData) +
                '}';
    }
}
