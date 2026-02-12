package com.example.app.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String gmailClientId;
    private final String gmailClientSecret;
    private final String gmailRefreshToken;
    private final String emailFrom;
    private final String smtpHost;
    private final int smtpPort;
    private final String smtpUser;
    private final String smtpPass;
    private final String publicBaseUrl;

    public EmailService(
        RestTemplateBuilder restTemplateBuilder,
        ObjectMapper objectMapper,
        @Value("${gmail.client-id:}") String gmailClientId,
        @Value("${gmail.client-secret:}") String gmailClientSecret,
        @Value("${gmail.refresh-token:}") String gmailRefreshToken,
        @Value("${app.email-from:}") String emailFrom,
        @Value("${smtp.host:}") String smtpHost,
        @Value("${smtp.port:587}") int smtpPort,
        @Value("${smtp.user:}") String smtpUser,
        @Value("${smtp.pass:}") String smtpPass,
        @Value("${app.public-base-url:http://localhost:8080}") String publicBaseUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
        this.gmailClientId = gmailClientId;
        this.gmailClientSecret = gmailClientSecret;
        this.gmailRefreshToken = gmailRefreshToken;
        this.emailFrom = emailFrom;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUser = smtpUser;
        this.smtpPass = smtpPass;
        this.publicBaseUrl = publicBaseUrl;
    }

    public void sendEmailVerification(String toEmail, String userId, String rawToken) {
        String verifyUrl = publicBaseUrl
            + "/verify-email.html?userId=" + urlEncode(userId)
            + "&token=" + urlEncode(rawToken);
        String subject = "Verify your email";
        String html = """
            <div style="font-family:Arial,sans-serif;line-height:1.6">
              <h2>Verify your account</h2>
              <p>Click the button below to verify your email address.</p>
              <p><a href="%s" style="padding:10px 16px;background:#111827;color:#fff;text-decoration:none;border-radius:8px">Verify Email</a></p>
              <p>If the button does not work, use this URL:</p>
              <p>%s</p>
            </div>
            """.formatted(verifyUrl, verifyUrl);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendOtpCode(String toEmail, String otpCode) {
        String subject = "Your login OTP code";
        String html = """
            <div style="font-family:Arial,sans-serif;line-height:1.6">
              <h2>Two-factor authentication</h2>
              <p>Use this code to finish your login:</p>
              <p style="font-size:28px;font-weight:bold;letter-spacing:4px">%s</p>
              <p>This code expires in a few minutes.</p>
            </div>
            """.formatted(otpCode);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        if (!canUseGmailOAuth() && !canUseSmtp()) {
            log.warn(
                "Email not sent (missing email config). Missing: {}. To: {}, subject: {}",
                missingConfigParts(),
                toEmail,
                subject
            );
            return;
        }

        if (canUseGmailOAuth()) {
            sendViaGmailOAuthSmtp(toEmail, subject, htmlBody);
            return;
        }

        sendViaSmtp(toEmail, subject, htmlBody);
    }

    private boolean canUseGmailOAuth() {
        return !gmailClientId.isBlank() && !gmailClientSecret.isBlank() && !gmailRefreshToken.isBlank() && !emailFrom.isBlank();
    }

    private boolean canUseSmtp() {
        return !smtpHost.isBlank() && !emailFrom.isBlank() && !smtpUser.isBlank() && !smtpPass.isBlank();
    }

    private void sendViaGmailOAuthSmtp(String toEmail, String subject, String htmlBody) {
        try {
            String accessToken = fetchAccessToken();
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(smtpHost.isBlank() ? "smtp.gmail.com" : smtpHost);
            sender.setPort(smtpPort);
            sender.setUsername(emailFrom);
            sender.setPassword(accessToken);

            java.util.Properties props = sender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
            props.put("mail.smtp.starttls.enable", smtpPort == 465 ? "false" : "true");
            props.put("mail.smtp.ssl.enable", smtpPort == 465 ? "true" : "false");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.writetimeout", "5000");

            jakarta.mail.internet.MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(Objects.requireNonNull(emailFrom));
            helper.setTo(Objects.requireNonNull(toEmail));
            helper.setSubject(Objects.requireNonNull(subject));
            helper.setText(Objects.requireNonNull(htmlBody), true);
            sender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to send email with Gmail SMTP OAuth2", e);
        }
    }

    private void sendViaSmtp(String toEmail, String subject, String htmlBody) {
        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(smtpHost);
            sender.setPort(smtpPort);
            sender.setUsername(smtpUser);
            sender.setPassword(smtpPass);

            java.util.Properties props = sender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.auth.mechanisms", "LOGIN PLAIN");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.writetimeout", "5000");

            jakarta.mail.internet.MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(Objects.requireNonNull(emailFrom));
            helper.setTo(Objects.requireNonNull(toEmail));
            helper.setSubject(Objects.requireNonNull(subject));
            helper.setText(Objects.requireNonNull(htmlBody), true);
            sender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to send email with SMTP", e);
        }
    }

    private String missingConfigParts() {
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (canUseGmailOAuth() || canUseSmtp()) {
            return "";
        }
        if (emailFrom.isBlank()) {
            missing.add("EMAIL_FROM");
        }
        if (gmailClientId.isBlank() || gmailClientSecret.isBlank() || gmailRefreshToken.isBlank()) {
            missing.add("GMAIL_CLIENT_ID/GMAIL_CLIENT_SECRET/GMAIL_REFRESH_TOKEN");
        }
        if (smtpHost.isBlank() || smtpUser.isBlank() || smtpPass.isBlank()) {
            missing.add("SMTP_HOST/SMTP_USER/SMTP_PASS");
        }
        return String.join(", ", missing);
    }

    private String fetchAccessToken() {
        String form = "client_id=" + urlEncode(gmailClientId)
            + "&client_secret=" + urlEncode(gmailClientSecret)
            + "&refresh_token=" + urlEncode(gmailRefreshToken)
            + "&grant_type=refresh_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(form, headers);

        String response = restTemplate.postForObject("https://oauth2.googleapis.com/token", request, String.class);
        if (response == null || response.isBlank()) {
            throw new IllegalStateException("Empty access token response");
        }
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode tokenNode = root.get("access_token");
            if (tokenNode == null || tokenNode.asText().isBlank()) {
                throw new IllegalStateException("Missing access_token in response");
            }
            return tokenNode.asText();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse access token response", e);
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
