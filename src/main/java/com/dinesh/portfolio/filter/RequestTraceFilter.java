package com.dinesh.portfolio.filter;

import com.dinesh.portfolio.exception.RequestProcessingException;
import com.dinesh.portfolio.util.Constant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTraceFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String traceId = request.getHeader(Constant.TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(Constant.TRACE_ID_KEY, traceId);
        response.setHeader(Constant.TRACE_ID_HEADER, traceId);

        long startTime = System.currentTimeMillis();
        try {
            log.info(
                    "Request started method={}, uri={}, clientIp={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr()
            );
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error(
                    "Unhandled exception during request processing method={}, uri={}, error={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage(),
                    ex
            );
            throw new RequestProcessingException("Error occurred while processing request", ex);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info(
                    "Request completed method={}, uri={}, status={}, duration={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );
            MDC.clear();
        }

    }
}
