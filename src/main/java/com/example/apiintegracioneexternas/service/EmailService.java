package com.example.apiintegracioneexternas.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String email;

    public void sendEmail(String emailTo){
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(email);
            helper.setTo(emailTo);
            helper.setSubject("Listado");
            helper.setText("texo de prueba");
            javaMailSender.send(message);


        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }




}
