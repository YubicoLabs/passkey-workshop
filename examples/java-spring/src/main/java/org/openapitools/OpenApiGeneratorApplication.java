package org.openapitools;

import com.fasterxml.jackson.databind.Module;

import java.io.InputStream;
import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

        setStorageProperties();

        SpringApplication.run(OpenApiGeneratorApplication.class, args);

    }

    private static void setStorageProperties() {
        // Determine if using in-mem or mysql
        String storageType = System.getenv("DATABASE_TYPE");

        System.out.println(storageType);

        if (storageType.equals("local")) {
            System.out.println("Here");

            System.setProperty("datasource.type", "local");

            // Used to prevent the application from attempting to establish a DB connection
            System.setProperty("spring.autoconfigure.exclude",
                    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration");

        } else if (storageType.equals("mysql")) {
            System.setProperty("datasource.type", "mysql");
            System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
            // @TODO - Consider making this an env variable
            System.setProperty("spring.datasource.url", "jdbc:mysql://host.docker.internal:3306/passkeyStorage");
            System.setProperty("spring.datasource.username", "root");

            /*
             * WARNING
             * This line is meant for demo purposes
             * Please ensure that you have a robust secret management process that is not
             * exposed on the app server
             */
            System.setProperty("spring.datasource.password", System.getenv("DATABASE_PASSWORD"));

            System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        } else {
            // Default will be to leverage the rules for in-mem
            System.setProperty("datasource.type", "local");

            // Used to prevent the application from attempting to establish a DB connection
            System.setProperty("spring.autoconfigure.exclude",
                    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration");
        }
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

    @Override
    public void run(String... args) throws Exception {
        /*
         * String[] beans = appContext.getBeanDefinitionNames();
         * 
         * Arrays.sort(beans);
         * for (String bean : beans) {
         * System.out.println(bean);
         * }
         */
    }

}