package architecture.training.market.inventorymanagement.domain.storage.items;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import architecture.training.market.inventorymanagement.domain.storage.StorageType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

public class StorageItem {
    private List<StorageType> allowedStorageTypes;
    private UUID itemId;

    private StorageItem() {
    }

    public static StorageItem create(UUID itemId, List<StorageType> allowedStorageTypes,
                                     DomainEventPublisher publisher) {
        var ev = new StorageItemCreatedEvent(itemId, allowedStorageTypes);
        var si = applyCreate(ev);
        publisher.publishEvent(ev);
        return si;
    }

    public static StorageItem applyCreate(StorageItemCreatedEvent ev) {
        if (CollectionUtils.isEmpty(ev.allowedStorageTypes()) || CollectionUtils.containsInstance(ev.allowedStorageTypes(), null)) {
            throw new IllegalArgumentException("Allowed storage types cannot be null or contain null values");
        }
        if (ev.itemId() == null) {
            throw new IllegalArgumentException("itemId cannot be null");
        }
        StorageItem item = new StorageItem();
        item.allowedStorageTypes = ev.allowedStorageTypes();
        item.itemId = ev.itemId();
        return item;

    }

    public UUID getItemId() {
        return this.itemId;
    }

    public boolean hasStorageType(UUID storageTypeId) {
        return this.allowedStorageTypes.stream().anyMatch(allowed -> allowed.getUuid().equals(storageTypeId));
    }

    public List<StorageType> getStorageTypes() {
        return this.allowedStorageTypes;
    }
}
