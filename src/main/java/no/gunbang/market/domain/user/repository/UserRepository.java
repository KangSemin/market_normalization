package no.gunbang.market.domain.user.repository;

import no.gunbang.market.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
