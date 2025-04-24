package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.List;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.StorageType;

public class StorageItem {
    private List<StorageType> allowedStorageTypes;
    private UUID itemId;
}
