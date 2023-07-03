package com.example.bureau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

@SpringBootApplication(exclude = {SecurityFilterAutoConfiguration.class})
public class BureauApplication {

    public static void main(String[] args) {
        SpringApplication.run(BureauApplication.class, args);
    }

/*@Bean
public CorsFilter corsFilter() {
   CorsConfiguration corsConfiguration = new CorsConfiguration();
   corsConfiguration.setAllowCredentials(true);
   corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5000"));
   corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
         "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
         "Access-Control-Request-Method", "Access-Control-Request-Headers"));
   corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
         "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
   corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
   UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
   urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
   return new CorsFilter(urlBasedCorsConfigurationSource);
}

@Bean
public CommonsRequestLoggingFilter requestLoggingFilter() {
   CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
   loggingFilter.setIncludeClientInfo(true);
   loggingFilter.setIncludeQueryString(true);
   loggingFilter.setIncludePayload(true);
   loggingFilter.setMaxPayloadLength(64000);
   return loggingFilter;
}*/

}
