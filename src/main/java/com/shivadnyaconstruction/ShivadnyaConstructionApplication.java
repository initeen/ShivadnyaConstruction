package com.shivadnyaconstruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShivadnyaConstructionApplication {

	private static final Logger logger = LoggerFactory.getLogger(ShivadnyaConstructionApplication.class);
	
	public static void main(String[] args) {
		String port = System.getenv("PORT");
        if (port != null) {
            System.setProperty("server.port", port);
        }
		SpringApplication.run(ShivadnyaConstructionApplication.class, args);
		logger.info("ShivadnyaConstructionApplication.main()");
	}

}
