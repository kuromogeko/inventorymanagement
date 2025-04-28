package architecture.training.market.inventorymanagement.domain.sales;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;

public record ItemSoldEvent(UUID itemId, ItemAmount amount) implements DomainEvent{
    
}
