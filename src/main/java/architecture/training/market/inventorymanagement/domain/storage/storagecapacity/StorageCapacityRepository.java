package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemStoredEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorageCapacityRepository {
    Optional<StorageCapacity> getCapacityById(UUID id);
    void deleteById(UUID id);
    List<StorageCapacity> findAll();

    void save(InventoryItemStoredEvent event);

    void save(ItemUnloadedEvent event);

    void save(StorageCapacityCreatedEvent event);
}
