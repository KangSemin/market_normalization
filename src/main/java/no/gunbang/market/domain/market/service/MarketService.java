package no.gunbang.market.domain.market.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.repository.MarketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;

    public Page<MarketListResponseDto> getPopulars(Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return marketRepository.findPopularTradeItems(startDate, pageable);
    }
}
