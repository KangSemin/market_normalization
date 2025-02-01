//package no.gunbang.market.datafortest;
//
//import no.gunbang.market.common.Inventory;
//import no.gunbang.market.common.InventoryRepository;
//import no.gunbang.market.common.Item;
//import no.gunbang.market.common.ItemRepository;
//import no.gunbang.market.common.Status;
//import no.gunbang.market.domain.auction.entity.Auction;
//import no.gunbang.market.domain.auction.entity.Bid;
//import no.gunbang.market.domain.auction.repository.AuctionRepository;
//import no.gunbang.market.domain.auction.repository.BidRepository;
//import no.gunbang.market.domain.market.entity.Market;
//import no.gunbang.market.domain.market.repository.MarketRepository;
//import no.gunbang.market.domain.user.entity.User;
//import no.gunbang.market.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//@Transactional
//public class InsertTestDataTest {
//
//    @Autowired private UserRepository userRepository;
//    @Autowired private ItemRepository itemRepository;
//    @Autowired private MarketRepository marketRepository;
//    @Autowired private AuctionRepository auctionRepository;
//    @Autowired private BidRepository bidRepository;
//    @Autowired private InventoryRepository inventoryRepository;
//    @Autowired private PasswordEncoder passwordEncoder;
//
//    @Test
//    @Commit
//    public void insertTestData() {
//        List<User> users = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            User user = TestDataFactory.createUser(
//                "user_" + i,
//                "server1",
//                (short) (10 + i),
//                "job",
//                1000L * i,
//                "user" + i + "@example.com",
//                passwordEncoder.encode("password")
//            );
//            users.add(userRepository.save(user));
//        }
//
//        List<Item> items = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            Item item = TestDataFactory.createItem("item_" + i);
//            items.add(itemRepository.save(item));
//        }
//
//        List<Market> markets = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            User user = users.get(i % users.size());
//            Item item = items.get(i % items.size());
//            Market market = TestDataFactory.createMarket(
//                10 * i,
//                100L * i,
//                Status.ON_SALE,
//                user,
//                item
//            );
//            markets.add(marketRepository.save(market));
//        }
//
//        List<Auction> auctions = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            User user = users.get(i % users.size());
//            Item item = items.get(i % items.size());
//            Auction auction = TestDataFactory.createAuction(
//                100L * i,
//                LocalDateTime.now().plusDays(i),
//                Status.ON_SALE,
//                user,
//                item
//            );
//            auctions.add(auctionRepository.save(auction));
//        }
//
//        List<Bid> bids = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            Auction auction = auctions.get(i % auctions.size());
//            User user = users.get(i % users.size());
//            Bid bid = TestDataFactory.createBid(
//                auction,
//                150L * i,
//                user
//            );
//            bids.add(bidRepository.save(bid));
//        }
//
//        List<Inventory> inventories = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            User user = users.get(i % users.size());
//            Item item = items.get(i % items.size());
//            Inventory inv = TestDataFactory.createInventory(
//                item,
//                user,
//                50L * i
//            );
//            inventories.add(inventoryRepository.save(inv));
//        }
//
//        Assertions.assertEquals(100, userRepository.count());
//        Assertions.assertEquals(100, itemRepository.count());
//        Assertions.assertEquals(100, marketRepository.count());
//        Assertions.assertEquals(100, auctionRepository.count());
//        Assertions.assertEquals(100, bidRepository.count());
//        Assertions.assertEquals(100, inventoryRepository.count());
//    }
//
//
//}
