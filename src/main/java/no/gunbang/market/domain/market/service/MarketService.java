package no.gunbang.market.domain.market.service;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.repository.MarketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MarketService {

    private final MarketRepository marketRepository;

    public Page<MarketResponseDto> getAllMarkets(int page, int size, String name) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Market> markets = marketRepository.findAllMarkets(name, pageable);

        return markets
            .map(market -> MarketResponseDto.builder()
                .id(market.getId())
                .amount(market.getAmount())
                .price(market.getPrice())
                .status(market.getStatus())
                .user(market.getUser())
                .item(market.getItem())
                .build());
    }
}
