package com.wigstore.wigstore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void send(String to, String subject, String body) {
        // PLACEHOLDER: prints to the console. Real sending needs spring-boot-starter-mail
        // and SMTP details from an email provider; we'll add that later.
        log.info("EMAIL to={} | {} | {}", to, subject, body);
    }
}