package com.howtodoinjava.rest.config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsClientBean {
    @Bean
    public StatsDClient metricsClient(){
        return new NonBlockingStatsDClient("csye6225", "localhost", 8125);
    }
}
