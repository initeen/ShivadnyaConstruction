package com.shivadnyaconstruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShivadnyaConstructionApplication {

	private static final Logger logger = LoggerFactory.getLogger(ShivadnyaConstructionApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ShivadnyaConstructionApplication.class, args);
		logger.info("ShivadnyaConstructionApplication.main()");
	}

}
