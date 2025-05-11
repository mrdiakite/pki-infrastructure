package com.pki.app.pki.infrastructure;

import com.pki.app.pki.infrastructure.model.LdapCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ldap.core.LdapTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SpringBootApplication
public class PkiInfrastructureApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(PkiInfrastructureApplication.class);

	@Autowired
	private LdapTemplate ldapTemplate;

	public static void main(String[] args) {
		SpringApplication.run(PkiInfrastructureApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<LdapCertificate> existingCerts = ldapTemplate.findAll(LdapCertificate.class);
		logger.info("Certificats chargés : " + existingCerts.size());
	}
}
