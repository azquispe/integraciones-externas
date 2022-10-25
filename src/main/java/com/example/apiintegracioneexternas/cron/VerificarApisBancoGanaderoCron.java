package com.example.apiintegracioneexternas.cron;

import com.example.apiintegracioneexternas.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VerificarApisBancoGanaderoCron {

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "*/10 * * * * *" )
    public void schedulerTaskVerificaApisBancoGanadero(){

        // AUN NO FUNCIONA EL CRON

        /*long now = System.currentTimeMillis()/1000;
        System.out.println("Schedule task using cron jobs = "+now);
        emailService.sendEmail("alvaro20092004@hotmail.com");*/

    }
}
