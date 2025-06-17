package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.List;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.storage.StorageType;

public record StorageItemCreatedEvent(UUID itemId, List<StorageType> allowedStorageTypes) implements DomainEvent {

    @Override
    public String toString() {
        return "StorageItemCreatedEvent{" +
               "itemId=" + itemId +
               ", allowedStorageTypes=" + allowedStorageTypes +
               '}';
    }
}
