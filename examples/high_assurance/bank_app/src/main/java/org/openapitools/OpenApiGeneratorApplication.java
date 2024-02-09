package org.openapitools;

import com.fasterxml.jackson.databind.Module;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@SpringBootApplication
@ComponentScan(basePackages = { "org.openapitools", "org.openapitools.configuration", "com.yubicolabs.bank_app" })
@EnableJpaRepositories(basePackages = "com.yubicolabs.bank_app")
@EntityScan(basePackages = "com.yubicolabs.bank_app")
@EnableAutoConfiguration
@SecurityScheme(name = "bearerAuth", scheme = "bearer", bearerFormat = "JWT", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class OpenApiGeneratorApplication {

  public static void main(String[] args) {
    try {
      // Properties props = loadPropertyFile();
      // System.out.println(props.toString());
      // setStorageProperties(props);
      setStorageProperties();

      SpringApplication.run(OpenApiGeneratorApplication.class, args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void setStorageProperties() {
    // Determine if using in-mem or mysql
    String storageType = System.getenv("DATABASE_TYPE");

    if (storageType.equals("local")) {
      System.setProperty("datasource.type", "local");

      // Used to prevent the application from attempting to establish a DB connection
      System.setProperty("spring.autoconfigure.exclude",
          "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration");

    } else if (storageType.equals("mysql")) {
      System.setProperty("datasource.type", "mysql");
      System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
      // @TODO - Consider making this an env variable
      System.setProperty("spring.datasource.url", "jdbc:mysql://172.17.0.1:3306/passkeyStorage");
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
    System.out.println(System.getProperties().toString());
  }

  private static Properties loadPropertyFile() throws Exception {
    try {
      InputStream inputStream;
      // Class path is found under WEB-INF/classes
      Properties prop = new Properties();
      String propFileName = "application.properties";

      inputStream = OpenApiGeneratorApplication.class.getClassLoader().getResourceAsStream(propFileName);
      // read the file
      if (inputStream != null) {
        prop.load(inputStream);
      } else {
        throw new Exception("property file '" + propFileName + "' not found in the classpath");
      }
      return prop;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Bean(name = "org.openapitools.OpenApiGeneratorApplication.jsonNullableModule")
  public Module jsonNullableModule() {
    return new JsonNullableModule();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * Autowired
       * Environment env;
       */
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        /*
         * Get origins list value from env variables
         */

        String originsVal = System.getenv("RP_ALLOWED_CROSS_ORIGINS");

        /*
         * Split the origins list by comma (as noted by the shell script)
         */

        String[] originsList = originsVal.split(",");

        /*
         * Iterate through origins list
         */

        Set<String> allowedOrigins = new HashSet<String>();

        for (int i = 0; i < originsList.length; i++) {
          allowedOrigins.add("http://" + originsList[i]);
          allowedOrigins.add("https://" + originsList[i]);
        }

        String[] result = allowedOrigins.stream().toArray(String[]::new);

        // String[] mappings = new String[] { "http://localhost:3000",
        // "http://localhost:8000" };
        registry.addMapping("/**").allowedOrigins(result).allowedMethods("GET", "PUT", "POST", "DELETE",
            "OPTIONS");
      }
    };
  }

}