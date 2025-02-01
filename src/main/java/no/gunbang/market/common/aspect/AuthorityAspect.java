package no.gunbang.market.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.common.exception.ForbiddenException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorityAspect {

    @Around("@annotation(LoginRequired)")
    public Object LoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest req = (HttpServletRequest) joinPoint.getArgs()[0];
        Long userId = (Long) req.getSession().getAttribute("userId");

        if (userId == null) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION);
        }

        return joinPoint.proceed();
    }
}
