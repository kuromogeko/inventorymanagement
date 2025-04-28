/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.List;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemStoredEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItem;

/**
 *
 * @author Admin
 */
public class StorageCapacity {

    private UUID id;
    private String name;
    private UUID storageTypeId;
    private ItemAmount maxCapacity;
    private ItemAmount remainingCapacity;
    private List<ItemStore> storedItems;

    private StorageCapacity() {
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
     * 
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

    public ItemAmount getRemainingCapacity(){
        return this.remainingCapacity;
    }

    public boolean canStore(StorageItem storageItem) {
        return storageItem.hasStorageType(this.storageTypeId);
    }

    // We trust the event to always be valid! So no checking the storageType
    public void applyStore(InventoryItemStoredEvent event) {
        try {
            this.remainingCapacity = this.remainingCapacity.subtract(event.amount());
            this.storedItems.add(new ItemStore(event.storageItemId(), event.amount()));
        } catch (CapacityBelowZeroException e) {
            // TODO This should cause a compensating event, but for now this is not our
            // focus and it is enough to know it happened
            e.printStackTrace();
        }
    }
}
