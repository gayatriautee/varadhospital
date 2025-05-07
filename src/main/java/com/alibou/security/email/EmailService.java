package com.alibou.security.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendRegistrationEmail(String to, String name, String username, String department, String appointmentDate) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("username", username);
        context.setVariable("email", to);
        context.setVariable("department", department);
        context.setVariable("appointmentDate",appointmentDate);
//        context.setVariable("appointmentTime", null);
        context.setVariable("location","Belgaum  karnataka karnataka karnataka karnataka karnataka karnataka");

        String html = templateEngine.process("registration-email", context);
        helper.setFrom("manojhc110@gmail.com", "YourClinic Appointments");
        helper.setReplyTo("noreply@example.com", "Do Not Reply");
        helper.setTo(to);
        helper.setSubject("Registration Successful");
        helper.setText(html, true);

        // Attach image inline
        FileSystemResource image = new FileSystemResource("src/main/resources/static/OIP.jpg");
        helper.addInline("logoImage", image);
        // ID must match 'cid' in HTML


        mailSender.send(message);
    }
}

