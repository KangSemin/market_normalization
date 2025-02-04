package no.gunbang.market;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.ItemRepository;
import no.gunbang.market.domain.auction.dto.request.BidAuctionRequestDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.auction.repository.BidRepository;
import no.gunbang.market.domain.auction.service.AuctionService;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@Slf4j
@SpringBootTest
class AuctionServiceConCurrencyTest {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BidRepository bidRepository;

    Auction auction;
    List<User> userList = new ArrayList<>();
    List<User> bidderList = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        userList = userRepository.findAll();

        final User AUCTION_CREATOR = userList.get(0);
        final int ID_OF_AUCTION_CREATOR = 1;

        bidderList = userList.subList(
            ID_OF_AUCTION_CREATOR,
            userList.size()
        );

        Item item = itemRepository.findById(1L).get();

        auction = Auction.of(
            AUCTION_CREATOR,
            item,
            10L,
            7
        );
    }

    @AfterEach
    public void afterEach() {
        bidRepository.deleteAllByAuction(auction);
        auctionRepository.delete(auction);
    }

    @WithMockUser
    @Test
    @DisplayName("락 없이 입찰 테스트 - 락을 걸어야 하는 이유")
    void testBidAuctionWithoutLock() throws InterruptedException {
        // 초기 입찰자 수를 알아야 함
        // 입찰하려고 하는 금액이 순차적으로 증가해야 함
        // 모두 입찰에 성공하는 케이스를 가정
        // 초기 입찰자 수 + 입찰하려고 한 횟수 == 최종 입찰자 수
        // 동시성 테스트를 하려면 실제 객체를 가지고 해야 한다.

        // given
        auctionRepository.save(auction);

        int initialBidderCount = auction.getBidderCount();

        int totalRequests = 7;
        int totalThreads = 3;
        long bidPrice = 20L;

        CountDownLatch latch = new CountDownLatch(totalRequests);

        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        // when
        for (int i = 0; i < totalRequests; i++) {
            int finalI = i;

            executorService.execute(
                () -> {
                    try {
                        User bidder = bidderList.get(finalI % bidderList.size());

                        BidAuctionRequestDto sampleBidRequest = new BidAuctionRequestDto(
                            auction.getId(),
                            bidPrice + (long) finalI
                        );

                        auctionService.bidAuction(
                            bidder.getId(),
                            sampleBidRequest
                        );

                    } finally {
                        latch.countDown();
                    }
                }
            );
        }

        latch.await();

        Auction updatedAuction = auctionRepository.findById(auction.getId()).get();

        // then
        assertThat(updatedAuction.getBidderCount())
            .isEqualTo(initialBidderCount + totalRequests);
    }
}