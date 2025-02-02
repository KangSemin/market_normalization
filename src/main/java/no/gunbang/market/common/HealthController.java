package no.gunbang.market.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actuator/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("");
    }
}