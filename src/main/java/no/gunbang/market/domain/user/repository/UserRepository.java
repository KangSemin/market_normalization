package no.gunbang.market.domain.user.repository;

import no.gunbang.market.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Long findIdByEmail(String email);

    @Query("SELECT u.password FROM User u WHERE u.id = :id")
    String findPasswordById(Long id);
}
