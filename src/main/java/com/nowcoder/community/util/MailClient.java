package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    /**
     * 发件人
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * SpringBoot Email 核心组件 负责发送邮件
     */
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 发送邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    public void sendMail(String to, String subject, String content) {

        try {
            //邮件实体类
            MimeMessage message = javaMailSender.createMimeMessage();
            //负责装配邮件
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            //以HTML格式显示
            mimeMessageHelper.setText(content, true);
            javaMailSender.send(message);

        } catch (MessagingException e) {
            logger.error("发送失败：" + e.getMessage());
        }
    }
}
