package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.items.BestBefore;

public record StorageRequest(UUID storageItemId, ItemAmount amount, BestBefore bestBefore, UUID storageCapacityId)  {

    public StorageRequest {
        if (storageItemId == null || amount == null || bestBefore == null || storageCapacityId == null) {
            throw new IllegalArgumentException("None of the arguments can be null");
        }
    }


}
