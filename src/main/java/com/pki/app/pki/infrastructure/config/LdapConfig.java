package com.pki.app.pki.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.core.LdapTemplate;

@Configuration
public class LdapConfig {

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://192.168.1.10:389");
        contextSource.setBase("dc=flash,dc=local");
        contextSource.setUserDn("cn=admin,dc=flash,dc=local");
        contextSource.setPassword("flash");
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}
