package com.ways.krbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    //see method autoSyncEmails() in src/main/java/com/ways/krbackend/service/EmailServiceImpl.java
}
