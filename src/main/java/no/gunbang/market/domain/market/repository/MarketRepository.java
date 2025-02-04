package no.gunbang.market.domain.market.repository;

import java.util.List;
import no.gunbang.market.domain.market.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarketRepository extends JpaRepository<Market, Long>, MarketRepositoryCustom {

    @Query("SELECT m FROM Market m "
        + "JOIN FETCH m.item "
        + "JOIN FETCH m.user "
        + "WHERE m.item.id = :itemId "
        + "AND m.status = 'ON_SALE' "
        + "ORDER BY m.price ASC, m.createdAt ASC")
    List<Market> findByItemIdOrderByPriceAscCreatedAtAsc(@Param("itemId") Long itemId);


}
