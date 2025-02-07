package no.gunbang.market.util;

import java.time.LocalDateTime;
import no.gunbang.market.common.entity.Item;
import no.gunbang.market.common.entity.Status;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
import no.gunbang.market.domain.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public class TestData {

    public static final User USER_ONE;
    public static final User USER_TWO;
    public static final User USER_THREE;
    public static final Item ITEM;
    public static final Auction AUCTION_BY_USER_ONE;
    public static final Bid BID_BY_USER_TWO;
    public static final long STARTING_PRICE = 9L;
    public static final long BID_PRICE = 10L;
    public static final int MIN_AUCTION_DAYS = 3;
    public static final int MAX_AUCTION_DAYS = 7;

    static {
        USER_ONE = new User();
        ReflectionTestUtils.setField(USER_ONE, "nickname", "one");
        ReflectionTestUtils.setField(USER_ONE, "server", "lostark");
        ReflectionTestUtils.setField(USER_ONE, "level", (short) 100);
        ReflectionTestUtils.setField(USER_ONE, "job", "warrior");
        ReflectionTestUtils.setField(USER_ONE, "gold", 1000000L);
        ReflectionTestUtils.setField(USER_ONE, "email", "one@example.com");
        ReflectionTestUtils.setField(USER_ONE, "password", "1234");
        ReflectionTestUtils.setField(USER_ONE, "id", 1L);

        USER_TWO = new User();
        ReflectionTestUtils.setField(USER_TWO, "nickname", "two");
        ReflectionTestUtils.setField(USER_TWO, "server", "lostark");
        ReflectionTestUtils.setField(USER_TWO, "level", (short) 200);
        ReflectionTestUtils.setField(USER_TWO, "job", "warrior");
        ReflectionTestUtils.setField(USER_TWO, "gold", 2000000L);
        ReflectionTestUtils.setField(USER_TWO, "email", "two@example.com");
        ReflectionTestUtils.setField(USER_TWO, "password", "1234");
        ReflectionTestUtils.setField(USER_TWO, "id", 2L);

        USER_THREE = new User();
        ReflectionTestUtils.setField(USER_THREE, "nickname", "three");
        ReflectionTestUtils.setField(USER_THREE, "server", "lostark");
        ReflectionTestUtils.setField(USER_THREE, "level", (short) 300);
        ReflectionTestUtils.setField(USER_THREE, "job", "warrior");
        ReflectionTestUtils.setField(USER_THREE, "gold", 3000000L);
        ReflectionTestUtils.setField(USER_THREE, "email", "three@example.com");
        ReflectionTestUtils.setField(USER_THREE, "password", "1234");
        ReflectionTestUtils.setField(USER_THREE, "id", 3L);

        ITEM = new Item();
        ReflectionTestUtils.setField(ITEM, "name", "지옥의 황천길에서 온 스크램블");
        ReflectionTestUtils.setField(ITEM, "id", 1L);

        AUCTION_BY_USER_ONE = new Auction();
        ReflectionTestUtils.setField(AUCTION_BY_USER_ONE, "user", USER_ONE);
        ReflectionTestUtils.setField(AUCTION_BY_USER_ONE, "item", ITEM);
        ReflectionTestUtils.setField(AUCTION_BY_USER_ONE, "startingPrice", STARTING_PRICE);
        ReflectionTestUtils.setField(AUCTION_BY_USER_ONE, "status", Status.ON_SALE);
        ReflectionTestUtils.setField(AUCTION_BY_USER_ONE, "id", 1L);

        LocalDateTime dueDate = LocalDateTime.now().plusDays(MAX_AUCTION_DAYS);

        ReflectionTestUtils.setField(AUCTION_BY_USER_ONE, "dueDate", dueDate);

        BID_BY_USER_TWO = new Bid();
        ReflectionTestUtils.setField(BID_BY_USER_TWO, "user", USER_TWO);
        ReflectionTestUtils.setField(BID_BY_USER_TWO, "auction", AUCTION_BY_USER_ONE);
        ReflectionTestUtils.setField(BID_BY_USER_TWO, "bidPrice", BID_PRICE);
    }
}
