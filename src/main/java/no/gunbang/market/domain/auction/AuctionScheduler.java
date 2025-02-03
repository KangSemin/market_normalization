package no.gunbang.market.domain.auction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;

    @Scheduled(cron = "0 */20 * * * *")
    @Transactional
    public void checkExpiredAuctions() {

        List<Auction> auctionList = new ArrayList<>();

        auctionList = auctionRepository.findByDueDateBefore(LocalDateTime.now());

        auctionList.forEach(
            auction -> {
                makeExpiredAuctionCompleted(auction);
                auctionRepository.save(auction);
            }
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void makeExpiredAuctionCompleted(Auction auction) {
        auction.makeExpiredAuctionCompleted();
        auctionRepository.save(auction);
    }
}