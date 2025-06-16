package architecture.training.market.inventorymanagement.domain.storage.inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {
    Optional<Inventory> findById(UUID id);
}
