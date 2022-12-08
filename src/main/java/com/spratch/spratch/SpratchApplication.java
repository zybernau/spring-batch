package com.spratch.spratch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpratchApplication {

	public static void main(String[] args) {
		 System.exit(
		 	SpringApplication.exit(
				SpringApplication.run(SpratchApplication.class, args)
		 	)
		 );
	}

}
