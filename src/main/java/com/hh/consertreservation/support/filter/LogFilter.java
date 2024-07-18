package com.hh.consertreservation.support.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@WebFilter(urlPatterns = "/api/*")
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        /**
         * request body 를 interceptor 에서 사용을 해야하고,
         * controller 에서 @RequestBody 로 받기위해
         * filter 에서부터 CashedBodyRequestServletWrapper 를 만들어 넘긴다.
         */
        CashedBodyRequestServletWrapper httpServletRequestWrapper = new CashedBodyRequestServletWrapper(httpRequest);
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        RequestLogging(httpServletRequestWrapper);

        chain.doFilter(httpServletRequestWrapper, httpServletResponse);

        ResponseLogging(httpServletResponse);
    }

    private void RequestLogging(CashedBodyRequestServletWrapper req) throws IOException {
        log.info("Request URI : " + req.getMethod() + " " + req.getRequestURI());
        log.info("Request Header : " + Collections.list(req.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, req::getHeader)));
        // parameter 로 요청올때 로깅
        if (req.getParameterMap().size() > 0) {
            String params = req.getParameterMap().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                    .collect(Collectors.joining(", "));
            log.info("Request Parameters : " + params);
        }

        String reqContent = new String(req.getInputStream().readAllBytes());

        if (StringUtils.hasText(reqContent)) {
            log.info("Request Content : \r\n" + reqContent);
        }
    }

    private void ResponseLogging(ContentCachingResponseWrapper resp) throws IOException {
        String resContent = new String(resp.getContentAsByteArray());
        resp.copyBodyToResponse();

        log.info("Response Status : " + resp.getStatus());
        log.info("Response Content : " + resContent);
    }
}
