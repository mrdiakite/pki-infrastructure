package com.pki.app.pki.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class LdapHealthService {
    @Autowired
    private LdapContextSource contextSource;

    public Map<String, String> checkLdapStatus() {
        Map<String, String> status = new HashMap<>();
        try {
            contextSource.getContext("cn=admin", "password"); // Test de connexion
            status.put("status", "CONNECTED");
            status.put("timestamp", Instant.now().toString());
        } catch (Exception e) {
            status.put("status", "DISCONNECTED");
            status.put("error", e.getMessage());
        }
        return status;
    }
}
