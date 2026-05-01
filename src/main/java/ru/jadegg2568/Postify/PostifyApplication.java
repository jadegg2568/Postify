package ru.jadegg2568.Postify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PostifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostifyApplication.class, args);
	}

}
