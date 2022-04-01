package nl.hetckm.base.dao;

import nl.hetckm.base.exceptions.EntityNotFoundException;
import nl.hetckm.base.exceptions.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class HttpClientErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus httpStatus = response.getStatusCode();
        return httpStatus.isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            throw new EntityNotFoundException(Object.class);
        } else if (response.getStatusCode().is5xxServerError()) {
            throw new ServiceUnavailableException("");
        } else {
            String body = new BufferedReader(
                    new InputStreamReader(response.getBody()))
                    .lines().collect(Collectors.joining("\n"));
            throw new RestClientException(body);
        }
    }
}
