package no.gunbang.market.common.entity;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByUserIdAndItemId(Long userId, Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.user.id = :userId and i.item.id = :itemId")
    Inventory findByUserIdAndItemIdForUpdate(@Param("userId") Long userId, @Param("itemId") Long itemId);
}
