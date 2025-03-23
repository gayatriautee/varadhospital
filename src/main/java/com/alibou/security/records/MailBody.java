package com.alibou.security.records;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
