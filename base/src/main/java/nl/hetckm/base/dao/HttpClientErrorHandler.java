package nl.hetckm.base.dao;

import nl.hetckm.base.exceptions.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import java.io.IOException;

public class HttpClientErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus httpStatus = response.getStatusCode();
        return httpStatus.isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) {
        throw new ServiceUnavailableException(response.getHeaders().getOrigin());
    }
}
