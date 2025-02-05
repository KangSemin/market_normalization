package no.gunbang.market.domain.market.service;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Inventory;
import no.gunbang.market.common.InventoryRepository;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void updateInventory(Item item, User user, int amount) {

        Inventory inventory = inventoryRepository
            .findByUserIdAndItemIdForUpdate(user.getId(), item.getId())
            .orElse(null);

        if (inventory == null) {
            if (amount < 0) {
                throw new CustomException(ErrorCode.LACK_OF_SELLER_INVENTORY);
            }
            inventory = new Inventory(item, user, amount);
            inventoryRepository.save(inventory);
            return;
        }

        int newAmount = inventory.getAmount() + amount;
        if (newAmount < 0) {
            throw new CustomException(ErrorCode.LACK_OF_SELLER_INVENTORY);
        }
        inventory.setAmount(newAmount);
    }
}
