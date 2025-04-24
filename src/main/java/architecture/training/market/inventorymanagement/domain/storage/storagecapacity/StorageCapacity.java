/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;

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
}
