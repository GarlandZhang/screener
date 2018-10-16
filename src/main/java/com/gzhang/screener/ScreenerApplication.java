package com.gzhang.screener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScreenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScreenerApplication.class, args);
	}
}
