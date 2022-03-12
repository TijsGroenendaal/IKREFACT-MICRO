package nl.hetckm.bouncer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan("nl.hetckm.base.model")
@ComponentScan("nl.hetckm")
public class BouncerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BouncerApplication.class, args);
    }

}
