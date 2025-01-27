package com.warehub.warehub;

import com.warehub.warehub.infrastructure.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RsaKeyConfigProperties.class})
public class WarehubApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarehubApplication.class, args);
	}

}
