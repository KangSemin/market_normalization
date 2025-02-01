package no.gunbang.market.domain.market.repository;

import no.gunbang.market.common.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
