package no.gunbang.market.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.user.dto.LoginRequestDto;
import no.gunbang.market.domain.user.service.SessionAndCookieService;
import no.gunbang.market.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SessionAndCookieService sessionAndCookieService;

    @PostMapping("/login")
    public ResponseEntity<String> login(
        HttpServletRequest req,
        HttpServletResponse res,
        @RequestBody LoginRequestDto loginRequestDto
    ){
        Long userId = userService.login(loginRequestDto);
        sessionAndCookieService.remember(req, res, userId);
        return ResponseEntity.ok("로그인 성공");
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(
        HttpServletRequest req,
        HttpServletResponse res
    ){
        sessionAndCookieService.delete(req, res);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("로그아웃 완료");
    }
}
