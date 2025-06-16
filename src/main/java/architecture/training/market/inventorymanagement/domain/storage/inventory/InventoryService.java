package architecture.training.market.inventorymanagement.domain.storage.inventory;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final DomainEventPublisher publisher;
private final InventoryRepository inventoryRepository;
    public InventoryService(DomainEventPublisher publisher, InventoryRepository inventoryRepository) {
        this.publisher = publisher;
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory countInventory(InventoryRequest request) {
        return Inventory.create(request.itemCounts(), request.employeeId(), publisher);
    }

    @EventListener
    public void handleNewCount(InventoryCountedEvent event){
        inventoryRepository.saveEvent(event);
    }
}
