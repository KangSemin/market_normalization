package no.gunbang.market.unit.domain.auction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import no.gunbang.market.TestData;
import no.gunbang.market.common.entity.Item;
import no.gunbang.market.common.entity.ItemRepository;
import no.gunbang.market.common.entity.Status;
import no.gunbang.market.domain.auction.service.AuctionScheduler;
import no.gunbang.market.domain.auction.dto.request.AuctionRegistrationRequestDto;
import no.gunbang.market.domain.auction.dto.request.BidAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.response.AuctionRegistrationResponseDto;
import no.gunbang.market.domain.auction.dto.response.BidAuctionResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.auction.repository.BidRepository;
import no.gunbang.market.domain.auction.service.AuctionService;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuctionRepository auctionRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BidRepository bidRepository;

    @Mock
    AuctionScheduler auctionScheduler;

    @InjectMocks
    AuctionService auctionService;

    User auctionRegistrant;
    User bidder;
    Item item;
    Auction auction;

    @BeforeEach
    void setUp() {
        auctionRegistrant = TestData.USER_ONE;
        item = TestData.ITEM;
        bidder = TestData.USER_TWO;

        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(auctionRegistrant));
    }

    @WithMockUser
    @Test
    @DisplayName("성공: 경매 등록")
    void succeedsToRegisterNewAuction() {
        // given
        long startingPrice = TestData.STARTING_PRICE;
        int auctionDays = TestData.MIN_AUCTION_DAYS;
        Long itemId = item.getId();
        Long userId = auctionRegistrant.getId();

        AuctionRegistrationRequestDto requestDto = new AuctionRegistrationRequestDto(
            itemId,
            startingPrice,
            auctionDays
        );

        Auction newAuction = Auction.of(
            auctionRegistrant,
            item,
            startingPrice,
            auctionDays
        );

        when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(item));

        when(auctionRepository.save(any()))
            .thenReturn(newAuction);

        // when
        AuctionRegistrationResponseDto responseDto = auctionService.registerAuction(
            userId,
            requestDto
        );

        // then
        long expectedStartingPrice = requestDto.getStartingPrice();
        long actualStartingPrice = responseDto.getStartingPrice();

        assertEquals(expectedStartingPrice, actualStartingPrice);
        verify(auctionRepository).save(any());
    }

    @WithMockUser
    @Test
    @DisplayName("성공: 경매에 새로운 입찰")
    void succeedsToRegisterNewBid() {
        // given
        Long bidderId = bidder.getId();
        auction = TestData.AUCTION_BY_USER_ONE;
        Long auctionId = auction.getId();
        long bidPrice = auction.getStartingPrice() + 1;

        // when
        when(auctionRepository.findByIdAndStatus(
                anyLong(),
                any(Status.class)
            )
        ).thenReturn(Optional.of(auction));

        doNothing().when(auctionScheduler)
            .makeExpiredAuctionCompleted(any());

        BidAuctionRequestDto requestDto = new BidAuctionRequestDto(
            auctionId,
            bidPrice
        );

        Bid newBid = Bid.of(
            bidder,
            auction,
            bidPrice
        );

        when(bidRepository.save(any()))
            .thenReturn(newBid);

        BidAuctionResponseDto responseDto = auctionService.bidAuction(
            bidderId,
            requestDto
        );

        // then
        assertEquals(bidPrice, responseDto.getBidPrice());
        verify(bidRepository).save(any());
    }
}