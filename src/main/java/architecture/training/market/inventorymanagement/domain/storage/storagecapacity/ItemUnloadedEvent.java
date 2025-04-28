package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record ItemUnloadedEvent(UUID storageCapacityId, UUID storageItemId, ItemAmount amount) implements DomainEvent  {

}
