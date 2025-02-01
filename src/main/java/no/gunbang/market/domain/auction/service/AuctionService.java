package no.gunbang.market.domain.auction.service;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionService {

    public Page<AuctionListResponseDto> getPopulars(Pageable pageable) {

        return null;
    }

}
