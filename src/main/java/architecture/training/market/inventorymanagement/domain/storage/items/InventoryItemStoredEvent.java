package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;

public record InventoryItemStoredEvent(UUID storageItemId, UUID storageCapacityId, ItemAmount amount, BestBefore bestBefore) implements DomainEvent, InventoryItemEvent  {

}
