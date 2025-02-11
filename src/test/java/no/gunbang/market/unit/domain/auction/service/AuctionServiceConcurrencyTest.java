package no.gunbang.market.unit.domain.auction.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.entity.Item;
import no.gunbang.market.common.entity.ItemRepository;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.domain.auction.dto.request.BidAuctionRequestDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
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
class AuctionServiceConcurrencyTest {

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

    Auction auction; // 경매
    List<User> userList = new ArrayList<>(); // 사용자 목록
    List<User> bidderList = new ArrayList<>(); // 입찰자 목록
    Bid bid;

    // 각 테스트 전에 실행되는 설정
    @BeforeEach
    public void beforeEach() {
        // 모든 사용자 목록을 불러옴
        userList = userRepository.findAll();

        // 첫 번째 사용자로 경매 생성자 지정
        final User AUCTION_CREATOR = userList.get(0);
        final int ID_OF_AUCTION_CREATOR = 1;

        int auctionDays = 7; // 경매 기간 설정
        long startingPrice = 10L; // 경매 시작 가격 설정
        long initialBidPrice = 15L; // 생성된 입찰 가격 설정

        // 경매에 입찰할 사용자 목록
        // 경매 생성자는 제외함
        bidderList = userList.subList(
            ID_OF_AUCTION_CREATOR,
            userList.size()
        );

        final User FIRST_BIDDER = bidderList.get(0);

        // 1번 아이템 조회
        Item item = itemRepository.findById(1L).orElseThrow();

        // 경매 객체 생성
        auction = Auction.of(
            AUCTION_CREATOR,
            item,
            startingPrice,
            auctionDays
        );

        bid = Bid.of(
            FIRST_BIDDER,
            auction,
            initialBidPrice
        );

        auctionRepository.save(auction); // 경매 저장
        bidRepository.save(bid); // 입찰 저장
    }

    // 각 테스트 후 실행되는 설정
    @AfterEach
    public void afterEach() {
        // 경매에 등록된 입찰 전체 삭제
        bidRepository.deleteAllByAuction(auction);
        // 생성된 경매 삭제
        auctionRepository.delete(auction);
    }

    @WithMockUser
    @Test
    @DisplayName("비관적 락 적용 후 입찰 테스트")
    void testBidAuctionWithPessimisticLock() throws InterruptedException {
        // given
        // 초기 입찰자 수
        int initialBidderCount = auction.getBidderCount();

        AtomicInteger totalOccurredExceptions = new AtomicInteger(0);
        // 동시성 문제 때문에 순서대로 계산되지 않을 거라서

        int totalRequests = 100; // 총 요청 수
        int totalThreads = 20; // 총 스레드 수
        long bidPrice = 20L; // 초기 입찰 금액
        Long auctionId = auction.getId(); // 경매 식별자

        // CountDownLatch를 이용하여 스레드 동기화
        // 모든 스레드가 완료될 때까지 대기
        CountDownLatch latch = new CountDownLatch(totalRequests);

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        // when
        // 총 요청 수만큼 스레드 실행
        for (int i = 0; i < totalRequests; i++) {
            int bidRequestIndex = i; // 요청 인덱스

            // 입찰 증가 금액
            // 요청마다 증가해야 하므로, 기본 금액에 요청 인덱스를 더함
            long bidIncrement = bidPrice + bidRequestIndex;

            // 각 스레드에서 입찰 요청 실행
            executorService.execute(
                () -> {
                    try {
                        log.info("현재 입찰 금액: {}",
                            bidRepository.findById(bid.getId()).get().getBidPrice());

                        // 입찰자 목록에서 입찰 요청에 해당하는 인덱스
                        // 예시) 입찰자가 총 9명이고 요청이 4번이라면,
                        //      4 % 9 = 4가 되는데,
                        //      인덱스는 0부터 세므로,
                        //      4 → 5번째 입찰자가 선택됨
                        int bidderIndex = bidRequestIndex % bidderList.size();

                        // 입찰자 목록에서 해당 인덱스로 조회한 입찰자 객체
                        User bidder = bidderList.get(bidderIndex);
                        Long bidderId = bidder.getId();

                        // 입찰 요청 DTO 생성
                        BidAuctionRequestDto requestDto = new BidAuctionRequestDto(
                            auctionId,
                            bidIncrement
                        );

                        // 입찰 요청 서비스 호출
                        auctionService.bidAuction(
                            bidderId,
                            requestDto
                        );

                        log.info("새로운 입찰 금액: {}", bidIncrement);
                    } catch (CustomException e) {
                        log.info("에러 발생 시점의 현재 입찰 금액: {}",
                            bidRepository.findById(bid.getId()).get().getBidPrice());
                        log.info("에러 발생 시점의 새로운 입찰 금액: {}", bidIncrement);

                        totalOccurredExceptions.getAndIncrement();
                    } finally {
                        // 작업이 끝나면 countDown 호출
                        // 기다리는 스레드 수를 하나씩 차감
                        latch.countDown();
                    }
                }
            );
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // 스레드 풀 종료
        executorService.shutdown();

        // 입찰이 완료된 후 경매 정보 다시 조회
        Auction updatedAuction = auctionRepository.findById(auctionId).orElseThrow();

        log.info("초기 요청 수: {}", totalRequests);
        log.info("발생한 예외 총 개수: {}", totalOccurredExceptions);
        log.info("성공한 요청 총 개수: {}", updatedAuction.getBidderCount());

        // then
        // 최종 입찰자 수가 '(초기 입찰자 수 + 총 요청 수) - 발생한 예외 총 개수'와 같은지 확인
        assertThat(updatedAuction.getBidderCount())
            .isEqualTo((initialBidderCount + totalRequests) - totalOccurredExceptions.get());
    }
}