package no.gunbang.market.domain.user.controller;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(

    ){
        userService.login();
        return ResponseEntity.ok("로그인 성공");
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(

    ){
        userService.logout();
        return ResponseEntity.noContent().build();
    }
}
