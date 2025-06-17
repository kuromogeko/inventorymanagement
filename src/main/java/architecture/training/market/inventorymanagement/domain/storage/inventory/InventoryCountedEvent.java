package architecture.training.market.inventorymanagement.domain.storage.inventory;

import java.util.List;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record InventoryCountedEvent(UUID id, List<InventoryCount> itemCounts, UUID employeeId) implements DomainEvent  {

    @Override
    public String toString() {
        return "InventoryCountedEvent{" +
               "id=" + id +
               ", itemCounts=" + itemCounts +
               ", employeeId=" + employeeId +
               '}';
    }
}
