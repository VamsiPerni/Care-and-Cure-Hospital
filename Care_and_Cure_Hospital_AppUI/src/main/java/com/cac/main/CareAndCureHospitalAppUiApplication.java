package com.cac.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.cac")
@EntityScan("com.cac.entity")
public class CareAndCureHospitalAppUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CareAndCureHospitalAppUiApplication.class, args);
	}

}
