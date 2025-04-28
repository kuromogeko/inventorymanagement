package architecture.training.market.inventorymanagement.domain.storage.inventory;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.items.BestBefore;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;

public record InventoryCount(UUID storageItemId, ItemAmount countedAmount, BestBefore soonestBestBefore){
    public InventoryCount {
        if (storageItemId == null || countedAmount == null || soonestBestBefore == null) {
            throw new IllegalArgumentException("All fields must be non-null");
        }
    }
}