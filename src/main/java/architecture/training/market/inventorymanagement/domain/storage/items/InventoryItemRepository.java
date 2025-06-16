package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository {
    Optional<InventoryItem> findById(UUID id);

    void saveEvent(InventoryItemBestBeforeChanged event);

    void saveEvent(InventoryItemStoredEvent event);

    void saveEvent(InventoryItemDecreasedEvent event);
}
