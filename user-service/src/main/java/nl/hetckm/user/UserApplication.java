package nl.hetckm.user;

import nl.hetckm.base.model.bouncer.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "nl.hetckm")
@EntityScan("nl.hetckm.base.model")
@RestController
@RequestMapping("/user")
public class UserApplication {

    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Autowired
    public UserApplication(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/username={username}")
    public AppUser getUserDetails(@PathVariable String username) {
        return userService.findOneByUsername(username);
    }

}
