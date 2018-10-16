package com.snj;

import com.snj.entities.Role;
import com.snj.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.snj.services.UserService;

import java.util.Arrays;

@SpringBootApplication
public class Application {

  /*  @Bean
    public CommandLineRunner setupDefaultUser(UserService service) {
        return args -> {
            service.save(new User(1,
                    "user",
                    "password",
                    Arrays.asList(new Role(1,"USER"), new Role(2, "ADMIN")),//roles
                    true
            ));
        };
    }*/

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
