package no.gunbang.market.domain.auction.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public Page<AuctionListResponseDto> getPopulars(Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return auctionRepository.findPopularBidItems(startDate, pageable);
    }

}
