package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemStoredEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItem;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author Admin
 */
public class StorageCapacity {

    private UUID id;
    private String name;
    private UUID storageTypeId;
    private ItemAmount maxCapacity;
    private ItemAmount remainingCapacity;
    private Set<ItemStore> storedItems;

    private StorageCapacity() {
        this.storedItems = new HashSet<>();
    }

    protected static StorageCapacity create(String name, UUID storageTypeId, ItemAmount maxCapacity,
                                            DomainEventPublisher publisher) {
        var ev = new StorageCapacityCreatedEvent(UUID.randomUUID(), name, storageTypeId, maxCapacity);
        var cap = applyCreate(ev);
        publisher.publishEvent(ev);
        return cap;
    }

    public static StorageCapacity applyCreate(StorageCapacityCreatedEvent ev) {
        if (ev.id() == null || ev.name() == null || ev.name().isEmpty() || ev.storageTypeId() == null
            || ev.maxCapacity() == null) {
            throw new IllegalArgumentException("Arguments cannot be null or empty");
        }
        var cap = new StorageCapacity();
        cap.id = ev.id();
        cap.name = ev.name();
        cap.storageTypeId = ev.storageTypeId();
        cap.maxCapacity = ev.maxCapacity();
        cap.remainingCapacity = ev.maxCapacity();
        return cap;
    }

    /**
     * @return true if no items are in this capacity
     */
    public boolean isUnloaded() {
        return this.remainingCapacity.equals(this.maxCapacity);
    }

    public UUID getId() {
        return id;
    }

    public UUID getStorageTypeId() {
        return storageTypeId;
    }

    public ItemAmount getRemainingCapacity() {
        return this.remainingCapacity;
    }

    public boolean canStore(StorageItem storageItem) {
        return storageItem.hasStorageType(this.storageTypeId);
    }

    // We trust the event to always be valid! So no checking the storageType
    public void applyStore(InventoryItemStoredEvent event) {
        try {
            this.remainingCapacity = this.remainingCapacity.subtract(event.amount());
            this.getItem(event.storageItemId()).ifPresentOrElse(
                    item -> {
                        var newAmount = item.amount().add(event.amount());
                        this.storedItems.remove(item);
                        this.storedItems.add(new ItemStore(event.storageItemId(), newAmount));
                    }, () -> this.storedItems.add(new ItemStore(event.storageItemId(), event.amount())));
        } catch (CapacityBelowZeroException e) {
            // TODO This should cause a compensating event, but for now this is not our
            // focus and it is enough to know it happened
            e.printStackTrace();
        }
    }

    public boolean hasItem(UUID storageItemId) {
        return this.storedItems.stream().anyMatch(storeItem -> storeItem.itemId().equals(storageItemId));
    }

    public Optional<ItemStore> getItem(UUID storageItemId) {
        return this.storedItems.stream().filter(storeItem -> storeItem.itemId().equals(storageItemId)).findFirst();
    }

    public void unload(UUID storageItemId, ItemAmount unloadAmount, DomainEventPublisher publisher)
            throws ItemNotInStoreException, UnloadGreaterThanStoredAmoundException {
        var item = this.getItem(storageItemId);
        if (item.isEmpty()) {
            throw new ItemNotInStoreException("Item not found in storage");
        }
        if (unloadAmount.greaterThan(item.get().amount())) {
            throw new UnloadGreaterThanStoredAmoundException("Unload amount cannot be greater than stored amount");
        }
        var event = new ItemUnloadedEvent(this.id, storageItemId, unloadAmount);
        this.applyUnload(event);
        publisher.publishEvent(event);
    }

    public void applyUnload(ItemUnloadedEvent event) {
        this.remainingCapacity.add(event.amount());
        if (this.remainingCapacity.greaterThan(this.maxCapacity)) {
            this.remainingCapacity = this.maxCapacity;
            // TODO LOG THIS AS A WARN
        }
        this.getItem(event.storageItemId()).ifPresent(storedItem -> {
            var remainingAmount = storedItem.amount().subtractSafe(event.amount());
            this.storedItems.remove(storedItem);
            if (remainingAmount.greaterThan(ItemAmount.ZERO)) {
                this.storedItems.add(new ItemStore(event.storageItemId(), remainingAmount));
            }
        });
    }


}
