package no.gunbang.market.domain.market.repository;

import java.util.List;
import no.gunbang.market.domain.market.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Long>, MarketRepositoryCustom {

    List<Market> findByItemIdOrderByPriceAsc(Long itemId);
}
