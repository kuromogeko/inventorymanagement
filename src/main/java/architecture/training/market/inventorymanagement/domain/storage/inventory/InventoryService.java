package architecture.training.market.inventorymanagement.domain.storage.inventory;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;

public class InventoryService {
    private final DomainEventPublisher publisher;

    public Inventory countInventory(InventoryRequest request) {
       return Inventory.create( request.itemCounts(),request.employeeId(), publisher);
    }
}
