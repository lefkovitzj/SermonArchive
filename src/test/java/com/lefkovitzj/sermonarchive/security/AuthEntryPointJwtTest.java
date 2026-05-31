package com.lefkovitzj.sermonarchive.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class AuthEntryPointJwtTest {

    @Test
    void commenceShouldSendUnauthorizedError() throws IOException, ServletException {
        AuthEntryPointJwt authEntryPointJwt = new AuthEntryPointJwt();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        authEntryPointJwt.commence(request, response, exception);

        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "401 Unauthorized");
    }
}
