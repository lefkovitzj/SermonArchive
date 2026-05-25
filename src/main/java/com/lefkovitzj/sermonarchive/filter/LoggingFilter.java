package com.lefkovitzj.sermonarchive.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            // Log the request (method and path).
            MDC.put("httpMethod", httpRequest.getMethod());
            MDC.put("requestUrl", httpRequest.getRequestURI());

            // Log the client's IP address, handling forwarding.
            String clientIp = httpRequest.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                // Not forwarded, so use the raw IP.
                clientIp = httpRequest.getRemoteAddr();
            } else {
                // Forwarded at least once, with the first X-Forwarded-For as the original client.
                clientIp = clientIp.split(",")[0].trim();
            }
            MDC.put("clientIp", clientIp);

            // Log a unique identifier.
            String traceId = httpRequest.getHeader("X-Trace-ID");
            if (traceId == null) {
                traceId = UUID.randomUUID().toString().substring(0, 8);
            }
            MDC.put("traceId", traceId);
        }

        try {
            chain.doFilter(request, response);
        }
        finally {
            if (response instanceof HttpServletResponse httpResponse) {
                // If known, include the status code.
                MDC.put("statusCode", String.valueOf(httpResponse.getStatus()));
                log.info("Request completed");
            }
            MDC.clear();
        }
    }
}
