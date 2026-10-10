package ru.itscan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ItscantgbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItscantgbotApplication.class, args);
	}

}
