package com.alibou.security.service.serviceImp;

import com.alibou.security.records.MailBody;
import com.alibou.security.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom("manojhc110@gmail.com");
        message.setReplyTo("no-reply@gmail.com");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        mailSender.send(message);
    }
}
