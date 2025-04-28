package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.items.BestBefore;

public record SimpleStorageRequest(UUID storageItemId, ItemAmount amount, BestBefore bestBefore)  {

}
