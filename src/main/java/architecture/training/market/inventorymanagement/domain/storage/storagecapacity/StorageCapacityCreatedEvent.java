package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record StorageCapacityCreatedEvent(UUID id, String name, UUID storageTypeId, ItemAmount maxCapacity) implements DomainEvent {
    @Override
    public String toString() {
        return "StorageCapacityCreatedEvent{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", storageTypeId=" + storageTypeId +
               ", maxCapacity=" + maxCapacity +
               '}';
    }
}

