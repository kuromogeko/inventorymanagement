package architecture.training.market.inventorymanagement.domain.storage.items;

public sealed interface InventoryItemEvent permits InventoryItemStoredEvent, InventoryItemBestBeforeChanged, InventoryItemDecreasedEvent {
    
}
