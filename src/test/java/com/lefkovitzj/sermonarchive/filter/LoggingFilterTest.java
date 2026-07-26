package com.lefkovitzj.sermonarchive.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private LoggingFilter loggingFilter;

    @Test
    void shouldLogAndPassRequestDownChain() throws ServletException, IOException {
        // Train the test case.
        when(request.getRequestURI()).thenReturn("/api/v1/sermon-media/");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("X-Trace-ID")).thenReturn("test-trace-id");
        when(response.getStatus()).thenReturn(200);

        loggingFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
