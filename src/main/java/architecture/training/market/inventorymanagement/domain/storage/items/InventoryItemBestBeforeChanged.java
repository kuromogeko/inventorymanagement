package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record InventoryItemBestBeforeChanged(UUID storageItemId, BestBefore bestBefore) implements DomainEvent, InventoryItemEvent{

    @Override
    public String toString() {
        return "InventoryItemBestBeforeChanged{" +
               "storageItemId=" + storageItemId +
               ", bestBefore=" + bestBefore +
               '}';
    }
}
