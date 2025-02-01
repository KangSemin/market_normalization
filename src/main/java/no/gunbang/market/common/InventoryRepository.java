package no.gunbang.market.common;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByUserIdAndItemId(Long userId, Long itemId);
}
