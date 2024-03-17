package com.roseno.curbcrime.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender javaMailSender;

    /**
     * 메일 전송
     * @param toEmail           전송 대상
     * @param subject           제목
     * @param content           내용
     * @throws MailException
     */
    public void send(String toEmail, String subject, String content) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("CurbCrime");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);

        javaMailSender.send(message);
    }
}
