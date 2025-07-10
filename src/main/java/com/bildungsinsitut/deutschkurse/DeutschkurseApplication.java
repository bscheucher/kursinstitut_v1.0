package com.bildungsinsitut.deutschkurse;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeutschkurseApplication {

	public static void main(String[] args) {
		// Load .env file INSIDE the main method
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(DeutschkurseApplication.class, args);
	}
}