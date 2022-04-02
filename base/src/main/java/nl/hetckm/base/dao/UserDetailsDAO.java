package nl.hetckm.base.dao;

import nl.hetckm.base.model.bouncer.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserDetailsDAO {

    @Value("${USER_DETAILS_SERVICE_PORT}")
    private String userDetailsPort;

    private final RestTemplate restTemplate;

    @Autowired
    public UserDetailsDAO() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new HttpClientErrorHandler());
    }

    public AppUser getUserDetails(String username) {
        return restTemplate.getForObject(
                "http://user-service:"+ userDetailsPort +"/user/username=" + username,
                AppUser.class
        );
    }
}
