package com.alibou.security.service;

import com.alibou.security.records.MailBody;

public interface EmailService {
    public void sendSimpleMessage(MailBody mailBody);
}
