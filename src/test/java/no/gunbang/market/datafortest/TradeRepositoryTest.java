//package no.gunbang.market.datafortest;
//
//import no.gunbang.market.domain.market.entity.Market;
//import no.gunbang.market.domain.market.entity.Trade;
//import no.gunbang.market.domain.market.repository.MarketRepository;
//import no.gunbang.market.domain.market.repository.TradeRepository;
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
//public class TradeRepositoryTest {
//
//    @Autowired
//    private TradeRepository tradeRepository;
//
//    @Autowired
//    private MarketRepository marketRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private final Random random = new Random();
//
//    @Test
//    @Commit
//    void create500Trades() {
//        List<Market> markets = marketRepository.findAll();
//        List<User> users = userRepository.findAll();
//
//        assertThat(markets.size()).isGreaterThan(0);
//        assertThat(users.size()).isGreaterThan(0);
//
//        IntStream.range(0, 500).forEach(i -> {
//            Market randomMarket = markets.get(random.nextInt(markets.size()));
//            User randomUser = users.get(random.nextInt(users.size()));
//
//            Trade trade = new Trade();
//            ReflectionTestUtils.setField(trade, "user", randomUser);
//            ReflectionTestUtils.setField(trade, "market", randomMarket);
//            ReflectionTestUtils.setField(trade, "amount", random.nextInt(50) + 1);
//            ReflectionTestUtils.setField(trade, "totalPrice", (random.nextInt(100) + 1) * 1000L);
//
//            tradeRepository.save(trade);
//        });
//    }
//}
