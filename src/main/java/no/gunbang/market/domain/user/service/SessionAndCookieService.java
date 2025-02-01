package no.gunbang.market.domain.user.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionAndCookieService {

    public void remember(HttpServletRequest req, HttpServletResponse res, Long userId) {
        //기존 세션 제거
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession = req.getSession(true);
        newSession.setAttribute("userId", userId);
        newSession.setMaxInactiveInterval(1800);

        Cookie rememberMeCookie = new Cookie("rememberMe", String.valueOf(userId));
        rememberMeCookie.setSecure(true);
        rememberMeCookie.setHttpOnly(true);
        rememberMeCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        rememberMeCookie.setPath("/");
        res.addCookie(rememberMeCookie);
    }

    public void delete(HttpServletRequest req, HttpServletResponse res) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie rememberMeCookie = new Cookie("rememberMe", null);
        rememberMeCookie.setMaxAge(0);
        rememberMeCookie.setPath("/");
        res.addCookie(rememberMeCookie);
    }
}
