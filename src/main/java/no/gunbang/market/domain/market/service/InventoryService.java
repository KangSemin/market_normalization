package no.gunbang.market.domain.market.service;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Inventory;
import no.gunbang.market.common.InventoryRepository;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.common.lock.LockStrategy;
import no.gunbang.market.domain.user.entity.User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final LockStrategy lockStrategy;

    public void updateInventory(Item item, User user, int amount) {

        Inventory inventory = inventoryRepository.findByUserIdAndItemId(user.getId(), item.getId())
            .orElse(null);

        // 아이템 갖고있지 않았다면 판매자는 예외 구매자는 새로 객체 생성
        if (inventory == null) {

            if (amount < 0) {
                throw new CustomException(ErrorCode.LACK_OF_SELLER_INVENTORY);
            }
            inventory = new Inventory(item, user, amount);
            inventoryRepository.save(inventory);
            return;
        }

        final Inventory currentInventory = inventory;
        // 구매자는 아이템 수 증가 판매자는 감소 후 setter 호출
        lockStrategy.execute(Inventory.class, inventory.getId(), 1000L, 3000L, () -> {
            int newAmount = currentInventory.getAmount() + amount;
            if (newAmount < 0) {
                throw new CustomException(ErrorCode.LACK_OF_SELLER_INVENTORY);
            }
            currentInventory.setAmount(newAmount);
            inventoryRepository.save(currentInventory);
            return null;
        });
    }

}
