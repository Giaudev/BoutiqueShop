package edu.poly.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "edu.poly.shop.controller.admin", "edu.poly.shop.controller.site", "edu.poly.shop.service", "edu.poly.shop.repository", "edu.poly.shop.utils", "edu.poly.shop.config" })
public class BoutiqueShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoutiqueShopApplication.class, args);
	}

}
