package no.gunbang.market.unit.domain.auction.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import no.gunbang.market.TestData;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuctionTest {

    User auctionRegistrant;
    Item item;
    long startingPrice;

    @BeforeEach
    void setUp() {
        auctionRegistrant = TestData.USER_ONE;
        item = TestData.ITEM;
        startingPrice = TestData.STARTING_PRICE;
    }

    @Test
    @DisplayName("실패: 입력된 경매 희망 기간이 최소 기한보다 적으면 예외 발생")
    void failsToRegisterAuctionIfAuctionDaysAreLessThanMinimum() {
        // given
        int auctionDays = TestData.MIN_AUCTION_DAYS - 1;

        // when
        CustomException exception = assertThrows(
            CustomException.class,
            () -> {
                Auction.of(
                    auctionRegistrant,
                    item,
                    startingPrice,
                    auctionDays
                );
            }
        );

        // then
        String expectedMessage = ErrorCode.AUCTION_DAYS_OUT_OF_RANGE.getMessage();
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("실패: 입력된 경매 희망 기간이 최대 기한을 넘으면 예외 발생")
    void failsToRegisterAuctionIfAuctionDaysAreGreaterThanMaximum() {
        // given
        int auctionDays = TestData.MAX_AUCTION_DAYS + 1;

        // when
        CustomException exception = assertThrows(
            CustomException.class,
            () -> {
                Auction.of(
                    auctionRegistrant,
                    item,
                    startingPrice,
                    auctionDays
                );
            }
        );

        // then
        String expectedMessage = ErrorCode.AUCTION_DAYS_OUT_OF_RANGE.getMessage();
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}