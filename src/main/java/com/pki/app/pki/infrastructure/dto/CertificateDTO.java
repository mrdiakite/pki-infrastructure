package com.pki.app.pki.infrastructure.dto;

import lombok.Data;

@Data
public class CertificateDTO {
    private String cn;
    private String o;
    private String ou;
    private String l;
    private String st;
    private String c;
    private String email;
    private int validityDays;
}
