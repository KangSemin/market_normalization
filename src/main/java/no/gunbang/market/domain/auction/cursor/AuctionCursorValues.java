package no.gunbang.market.domain.auction.cursor;

import java.time.LocalDateTime;

public record AuctionCursorValues(
        Long lastStartPrice,
        Long lastCurrentMaxPrice,
        LocalDateTime lastDueDate
) {

}
