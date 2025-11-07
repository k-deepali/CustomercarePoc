package com.agsft.customer.Care.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * This is Custom message configuration
 * @author Pranjal
 */
@Configuration
public class MessageConfiguration {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:message/error_message",
                "classpath:message/success_message",
                "classpath:message/validation_message"
        );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    }