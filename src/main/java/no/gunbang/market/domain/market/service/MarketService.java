package no.gunbang.market.domain.market.service;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketService {

    public Page<MarketListResponseDto> getPopulars(Pageable pageable) {

        return null;
    }
}
