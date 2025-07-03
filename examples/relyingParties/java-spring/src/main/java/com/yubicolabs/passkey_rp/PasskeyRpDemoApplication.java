package com.yubicolabs.passkey_rp;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PasskeyRpDemoApplication {

  public static void main(String[] args) {
    setStorageProperties();

    SpringApplication.run(PasskeyRpDemoApplication.class, args);
  }

  private static void setStorageProperties() {
    /*
     * Currently only support mysql, so default to the required mysql options
     * 
     * We are leaving this configurable if there is interest in expanding to other
     * DB options
     */
    System.setProperty("datasource.type", "mysql");
    System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
    System.setProperty("spring.datasource.url", "jdbc:mysql://host.docker.internal:3306/passkeyStorage");
    System.setProperty("spring.datasource.username", "root");
    System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");

    /*
     * WARNING
     * This line is meant for demo purposes
     * Please ensure that you have a robust secret management process that is not
     * exposed on the app server
     */
    System.setProperty("spring.datasource.password", System.getenv("DATABASE_PASSWORD"));
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
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
