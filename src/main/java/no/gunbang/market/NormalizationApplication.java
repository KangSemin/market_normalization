package no.gunbang.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NormalizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NormalizationApplication.class, args);
    }

}
