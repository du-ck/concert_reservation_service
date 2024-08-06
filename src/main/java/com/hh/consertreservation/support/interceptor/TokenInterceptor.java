package com.hh.consertreservation.support.interceptor;

import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.support.exception.TokenVerificationException;
import com.hh.consertreservation.support.filter.CashedBodyRequestServletWrapper;
import com.hh.consertreservation.support.util.ParserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;

/**
 * 토큰검증을 위한 interceptor
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenFacade tokenFacade;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //header 에서 "Queue-Token" 을 꺼내고,
        //parameter 혹은 body 에서 userId를 꺼낸 후 파사드로 보낸다.
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }

        CashedBodyRequestServletWrapper req = new CashedBodyRequestServletWrapper(request);

        String token = req.getHeader("Queue-Token");

        String reqBodyJson = new String(req.getInputStream().readAllBytes());

        String userId = StringUtils.hasText(req.getParameter("userId")) ? req.getParameter("userId") : ParserUtil.extractValueFromJson(reqBodyJson, "userId");

        if (!StringUtils.hasText(token)) {
            throw new TokenVerificationException("토큰이 없습니다");
        }
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId가 없습니다");
        }
        tokenFacade.verification(token);

        return true;
    }

}
