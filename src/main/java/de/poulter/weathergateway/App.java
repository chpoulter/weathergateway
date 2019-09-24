package de.poulter.weathergateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:weathergateway.properties")
public class App {
    
    private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Starting the weather gateway.");
        
        SpringApplication.run(App.class, args);
    }
}