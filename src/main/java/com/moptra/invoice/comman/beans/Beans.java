package com.moptra.invoice.comman.beans;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class Beans {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    public JavaMailSender javaMailSender(){
        return new JavaMailSenderImpl();
    }
}
