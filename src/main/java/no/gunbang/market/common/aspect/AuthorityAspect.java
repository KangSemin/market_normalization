package no.gunbang.market.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.common.exception.UnauthorizedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorityAspect {

    @Around("@annotation(no.gunbang.market.common.aspect.LoginRequired)")
    public Object checkLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getRequestFromArgs(joinPoint.getArgs());

        if (request == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_OPERATION);
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_OPERATION);
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest getRequestFromArgs(Object[] args) {
        return (HttpServletRequest) Arrays.stream(args)
            .filter(arg -> arg instanceof HttpServletRequest)
            .findFirst()
            .orElse(null);
    }
}
