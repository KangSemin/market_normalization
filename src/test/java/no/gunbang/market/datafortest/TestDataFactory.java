package no.gunbang.market.datafortest;

import no.gunbang.market.common.Inventory;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
import no.gunbang.market.domain.market.entity.Market;

import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;

public class TestDataFactory {

    public static no.gunbang.market.domain.user.entity.User createUser(String nickname, String server, short level, String job, long gold, String email, String password) {
        User user = new User();
        ReflectionTestUtils.setField(user, "nickname", nickname);
        ReflectionTestUtils.setField(user, "server", server);
        ReflectionTestUtils.setField(user, "level", level);
        ReflectionTestUtils.setField(user, "job", job);
        ReflectionTestUtils.setField(user, "gold", gold);
        ReflectionTestUtils.setField(user, "email", email);
        ReflectionTestUtils.setField(user, "password", password);
        return user;
    }

    public static Item createItem(String name) {
        Item item = new Item();
        ReflectionTestUtils.setField(item, "name", name);
        return item;
    }

    public static Market createMarket(int amount, long price, Status status, User user, Item item) {
        Market market = new Market();
        ReflectionTestUtils.setField(market, "amount", amount);
        ReflectionTestUtils.setField(market, "price", price);
        ReflectionTestUtils.setField(market, "status", status);
        ReflectionTestUtils.setField(market, "user", user);
        ReflectionTestUtils.setField(market, "item", item);
        return market;
    }

    public static Trade createTrade(User user) {
        Trade trade = new Trade();
        ReflectionTestUtils.setField(trade, "user", user);
        return trade;
    }

    public static Auction createAuction(long startingPrice, LocalDateTime dueDate, Status status, User user, Item item) {
        Auction auction = new Auction();
        ReflectionTestUtils.setField(auction, "startingPrice", startingPrice);
        ReflectionTestUtils.setField(auction, "dueDate", dueDate);
        ReflectionTestUtils.setField(auction, "status", status);
        ReflectionTestUtils.setField(auction, "user", user);
        ReflectionTestUtils.setField(auction, "item", item);
        return auction;
    }

    public static Bid createBid(Auction auction, long bidPrice, User user) {
        Bid bid = new Bid();
        ReflectionTestUtils.setField(bid, "auction", auction);
        ReflectionTestUtils.setField(bid, "bidPrice", bidPrice);
        ReflectionTestUtils.setField(bid, "user", user);
        return bid;
    }

    public static Inventory createInventory(Item item, User user, long amount) {
        Inventory inventory = new Inventory();
        ReflectionTestUtils.setField(inventory, "item", item);
        ReflectionTestUtils.setField(inventory, "user", user);
        ReflectionTestUtils.setField(inventory, "amount", amount);
        return inventory;
    }
}
