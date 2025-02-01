//package no.gunbang.market.datafortest;
//
//import no.gunbang.market.domain.auction.entity.Auction;
//import no.gunbang.market.domain.auction.entity.Bid;
//import no.gunbang.market.domain.auction.repository.AuctionRepository;
//import no.gunbang.market.domain.auction.repository.BidRepository;
//import no.gunbang.market.domain.user.entity.User;
//import no.gunbang.market.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Commit;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Random;
//import java.util.stream.IntStream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//public class BidRepositoryTest {
//
//    @Autowired
//    private BidRepository bidRepository;
//
//    @Autowired
//    private AuctionRepository auctionRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private final Random random = new Random();
//
//    @Test
//    @Commit
//    void create500Bids() {
//        List<Auction> auctions = auctionRepository.findAll();
//        List<User> users = userRepository.findAll();
//
//        assertThat(auctions.size()).isGreaterThan(0);
//        assertThat(users.size()).isGreaterThan(0);
//
//        IntStream.range(0, 500).forEach(i -> {
//            Auction randomAuction = auctions.get(random.nextInt(auctions.size()));
//            User randomUser = users.get(random.nextInt(users.size()));
//
//            Bid bid = new Bid();
//            ReflectionTestUtils.setField(bid, "auction", randomAuction);
//            ReflectionTestUtils.setField(bid, "user", randomUser);
//            ReflectionTestUtils.setField(bid, "bidPrice", (random.nextInt(100) + 1) * 1000L);
//
//            bidRepository.save(bid);
//        });
//    }
//}
