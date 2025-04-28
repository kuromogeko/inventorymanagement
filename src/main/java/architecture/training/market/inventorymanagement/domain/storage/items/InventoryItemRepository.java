package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository {
    Optional<InventoryItem> findById(UUID id);
}
