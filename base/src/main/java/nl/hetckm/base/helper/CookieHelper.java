package nl.hetckm.base.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieHelper {

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.cookie-secure}")
    private Boolean secureCookie;

    @Value("${jwt.cookie.restrict-site}")
    private Boolean sameSiteStrict;

    public HttpCookie createCookie(String value, long maxAge) {
        return ResponseCookie.from(cookieName, value)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(secureCookie)
                .sameSite(sameSiteStrict ? "Strict" : "Lax")
                .build();
    }
}
