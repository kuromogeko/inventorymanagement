package architecture.training.market.inventorymanagement.domain.storage.items;

import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacity;

public class InventoryItem {
    private BestBefore bestBefore;
    private ItemAmount amount;
    private StorageItem item;
    private StorageCapacity storagePlace;
    
}
