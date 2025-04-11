package com.yubicolabs.passkey_rp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocConfiguration {

	@Bean
	OpenAPI apiInfo() {
		return new OpenAPI()
				.info(
						new Info()
								.title("Passkey WebAuthn API by Yubico")
								.description("APIs to enable a WebAuthn application")
								.license(
										new License()
												.name("Apache 2.0")
												.url("https://www.apache.org/licenses/LICENSE-2.0.html"))
								.version("1.0.0"));
	}
}