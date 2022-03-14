package nl.hetckm.presetservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication(scanBasePackages = "nl.hetckm")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan(basePackages = "nl.hetckm.base.model.preset")
public class PresetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresetServiceApplication.class, args);
    }

}
