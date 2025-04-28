package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record InventoryItemBestBeforeChanged(UUID storageItemId, BestBefore bestBefore) implements DomainEvent{
    
}
