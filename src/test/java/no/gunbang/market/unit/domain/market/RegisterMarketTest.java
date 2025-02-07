package no.gunbang.market.unit.domain.market;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.entity.Inventory;
import no.gunbang.market.common.entity.InventoryRepository;
import no.gunbang.market.common.entity.Item;
import no.gunbang.market.common.entity.ItemRepository;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.market.dto.request.MarketRegistrationRequestDto;
import no.gunbang.market.domain.market.service.MarketService;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import no.gunbang.market.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class RegisterMarketTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private MarketService marketService;

    @Test
    void registerMarket_인벤토리_아이템_부족으로_예외발생() {
        // given
        Long userId = 1L;
        Long itemId = 100L;
        int requestAmount = 10;

        MarketRegistrationRequestDto requestDto = TestUtils.spy(MarketRegistrationRequestDto.class, Map.of(
            "itemId", itemId,
            "amount", requestAmount,
            "price", 1000L
        ));

        User user = TestUtils.spy(User.class, Map.of(
            "id", userId,
            "nickname", "판매자",
            "email", "seller@example.com"
        ));

        Item item = TestUtils.spy(Item.class, Map.of(
            "id", itemId,
            "name", "테스트 아이템"
        ));

        Inventory inventory = TestUtils.spy(Inventory.class, Map.of(
            "user", user,
            "item", item,
            "amount", 5
        ));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(inventoryRepository.findByUserIdAndItemIdForUpdate(userId, itemId)).thenReturn(inventory);

        // when & then
        assertThatThrownBy(() -> marketService.registerMarket(userId, requestDto))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining(ErrorCode.LACK_OF_SELLER_INVENTORY.getMessage());
    }


}
