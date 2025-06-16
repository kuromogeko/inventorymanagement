package architecture.training.market.inventorymanagement.application;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.inventory.Inventory;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryCountedEvent;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryRepository;
import org.springframework.stereotype.Component;

@Component
public class HashInventoryRepositoryImpl implements InventoryRepository {
    private final HashMap<UUID, InventoryCountedEvent> repo;

    public HashInventoryRepositoryImpl() {
        this.repo = new HashMap<>();
    }

    @Override
    public Optional<Inventory> findById(UUID id) {
        return Optional.ofNullable(this.repo.get(id)).map(Inventory::applyCreate);
    }

    @Override
    public void saveEvent(InventoryCountedEvent event) {
        repo.put(event.id(), event);
    }

}
