package nl.hetckm.presetservice;

import nl.hetckm.base.model.Preset;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication(scanBasePackages = "nl.hetckm")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan(basePackageClasses = Preset.class)
public class PresetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresetServiceApplication.class, args);
    }

}
