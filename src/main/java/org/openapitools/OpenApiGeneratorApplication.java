package org.openapitools;

import com.fasterxml.jackson.databind.Module;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = { "org.openapitools", "org.openapitools.configuration", "com.yubicolabs.passkey_rp" })
@EnableJpaRepositories(basePackages = "com.yubicolabs.passkey_rp")
@EntityScan(basePackages = "com.yubicolabs.passkey_rp")
@EnableAutoConfiguration
public class OpenApiGeneratorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OpenApiGeneratorApplication.class, args);
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

    @Autowired
    private ApplicationContext appContext;

    @Override
    public void run(String... args) throws Exception {

        String[] beans = appContext.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans) {
            System.out.println(bean);
        }

    }

}