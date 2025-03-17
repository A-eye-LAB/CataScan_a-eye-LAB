package org.cataract.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CataractAdminApplication {

	public static void main(String[] args) {

		try {
			SpringApplication.run(CataractAdminApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
