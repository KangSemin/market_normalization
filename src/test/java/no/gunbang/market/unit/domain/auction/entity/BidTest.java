package no.gunbang.market.unit.domain.auction.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import no.gunbang.market.TestData;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
import no.gunbang.market.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BidTest {

    User bidder;
    Auction auction;
    Bid bid;

    @BeforeEach
    void setUp() {
        bidder = TestData.USER_THREE;
        auction = TestData.AUCTION_BY_USER_ONE;
        bid = TestData.BID_BY_USER_TWO;
    }

    @Test
    @DisplayName("실패: 입찰가가 기존 금액과 동일하면 예외 발생")
    void failsToBidIfNewBidPriceIsSameAsCurrentBidPrice(){
        // given
        long bidPrice = TestData.BID_PRICE;

        // when
        CustomException thrownException = assertThrows(
            CustomException.class,
            () -> {
                bid.updateBid(
                    bidPrice,
                    bidder
                );
            }
        );

        // then
        String expectedMessage = ErrorCode.BID_TOO_LOW.getMessage();
        String actualMessage = thrownException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("실패: 같은 사용자가 연속으로 입찰하면 예외 발생")
    void failsToBidIfConsecutiveBiddingOccurs() {
        // given
        long firstBidPrice = TestData.BID_PRICE + 1;
        long secondBidPrice = firstBidPrice + 1;

        bid.updateBid(firstBidPrice, bidder);

        // when
        CustomException thrownException = assertThrows(
            CustomException.class,
            () -> {
                bid.updateBid(
                    secondBidPrice,
                    bidder
                );
            }
        );

        // then
        String expectedMessage = ErrorCode.CONSECUTIVE_BID_NOT_ALLOWED.getMessage();
        String actualMessage = thrownException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("실패: 사용자가 보유한 골드보다 입찰 금액이 높으면 예외 발생")
    void failsToBidIfBidPriceIsHigherThanHoldingGoldAmount() {
        // given
        long bidPrice = bidder.getGold() + 1;

        // when
        CustomException thrownException = assertThrows(
            CustomException.class,
            () -> {
                bid.updateBid(
                    bidPrice,
                    bidder
                );
            }
        );

        // then
        String expectedMessage = ErrorCode.EXCESSIVE_BID.getMessage();
        String actualMessage = thrownException.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}