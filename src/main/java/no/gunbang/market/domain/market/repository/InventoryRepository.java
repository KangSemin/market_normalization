package no.gunbang.market.domain.market.repository;

import java.util.Optional;
import no.gunbang.market.common.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByUserIdAndItemId(Long userId, Long itemId);
}
