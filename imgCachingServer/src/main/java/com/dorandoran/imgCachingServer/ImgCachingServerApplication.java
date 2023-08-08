package com.dorandoran.imgCachingServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ImgCachingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImgCachingServerApplication.class, args);
	}

}
