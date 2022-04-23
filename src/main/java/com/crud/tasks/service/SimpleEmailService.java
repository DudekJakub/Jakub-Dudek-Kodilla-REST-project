package com.crud.tasks.service;

import com.crud.tasks.domain.Mail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleEmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    private final MailCreatorService mailCreatorService;

    public void sendNewTrelloCardMail(final Mail mail) {
        log.info("Starting email preparation");
        try {
            javaMailSender.send(createNewTrelloCardMimeMessage(mail));

            if (mail.getToCc() == null) {
                log.info("Email has been sent (without specified Cc).");
            } else {
                log.info("Email has been sent.");
            }
        } catch (MailException e) {
            log.error("Failed to process email sending: " + e.getMessage(), e);
        }
    }

    public void sendTaskQntMail(final Mail mail) {
        log.info("Starting email preparation");
        try {
            javaMailSender.send(createTaskQntMimeMessage(mail));

            if (mail.getToCc() == null) {
                log.info("Email has been sent (without specified Cc).");
            } else {
                log.info("Email has been sent.");
            }
        }  catch (MailException e) {
            log.error("Failed to process email sending: " + e.getMessage(), e);
        }
    }

    public void simpleSend(final Mail mail) {
        log.info("Starting email preparation...");
        try {
            SimpleMailMessage mailMessage = createMailMessage(mail);
            javaMailSender.send(mailMessage);

            if (mail.getToCc() == null) {
                log.info("Email has been sent (without specified Cc).");
            } else {
                log.info("Email has been sent.");
            }
        } catch (MailException e) {
            log.error("Failed to process email sending: " + e.getMessage(), e);
        }
    }

    private SimpleMailMessage createMailMessage(final Mail mail) {
        Optional<String> optionalToCc = Optional.ofNullable(mail.getToCc());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mail.getMailTo());
        mailMessage.setSubject(mail.getSubject());
        mailMessage.setText(mail.getMessage());
        optionalToCc.ifPresent(mailMessage::setCc);
        return mailMessage;
    }

    private MimeMessagePreparator createNewTrelloCardMimeMessage(final Mail mail) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(mail.getMailTo());
            messageHelper.setSubject(mail.getSubject());
            messageHelper.setText(mailCreatorService.buildTrelloCardEmail(mail.getMessage()), true);
            };
        }

    private MimeMessagePreparator createTaskQntMimeMessage(final Mail mail) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(mail.getMailTo());
            messageHelper.setSubject(mail.getSubject());
            messageHelper.setText(mailCreatorService.buildTaskQntInformationEmail(mail.getMessage()), true);
        };
    }
}
