package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.log.LogService;
import org.example.expert.domain.common.log.LogStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DBLoggingAspect {

    private final HttpServletRequest request;
    private final LogService logService;

    @Around("@annotation(org.example.expert.aop.annotation.DBLoggingApi)")
    public Object logDBRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        String requestUrl = request.getRequestURI();
        Long userId = authUser.getId();
        String method = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            logService.saveLog(userId, requestUrl, method, LogStatus.SUCCESS);
            return result;
        } catch (Exception e) {
            logService.saveLog(userId, requestUrl, method, LogStatus.ERROR);
            throw e;
        }
    }
}
