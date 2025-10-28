package com.classroom.classroom_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration.class
})
public class ClassroomServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClassroomServiceApplication.class, args);
	}

}
