package com.codingshuttle.project.uber.uberApp;

import com.codingshuttle.project.uber.uberApp.services.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UberAppApplicationTests {

	@Autowired
	EmailSenderService emailSenderService;

	@Test
	void contextLoads() {
		emailSenderService.sendEmail(
				"tewono1309@24faw.com",
				"This is the testing mail",
				"This is the email body"
		);
	}


	@Test
	void sendEmailMultiple() {

		String emails[]={
				"tewono1309@24faw.com",
				"apuroop21@gmail.com"
		};

		emailSenderService.sendEmail(
				emails,
				"This is the testing mail",
				"This is the email body"
		);
	}

}
