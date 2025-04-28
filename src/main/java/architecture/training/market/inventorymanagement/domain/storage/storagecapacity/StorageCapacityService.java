package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import architecture.training.market.inventorymanagement.domain.StorageTypeService;
import architecture.training.market.inventorymanagement.domain.storage.StorageType;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemDecreasedEvent;

public class StorageCapacityService {
    private final StorageTypeService typeService;
    private final StorageCapacityRepository repository;
    private final DomainEventPublisher publisher;

    public StorageCapacityService(StorageTypeService typeService, StorageCapacityRepository repository,
            DomainEventPublisher publisher) {
        this.typeService = typeService;
        this.repository = repository;
        this.publisher = publisher;
    }

    public StorageCapacity createStorageCapacity(String name, UUID storageTypeId, ItemAmount maxCapacity) {
        var type = typeService.getStorageTypeById(storageTypeId);
        if (type.isEmpty()) {
            throw new IllegalArgumentException("Capacity needs to reference valid StorageType");
        }
        return StorageCapacity.create(name, storageTypeId, maxCapacity, publisher);
    }

    public void deleteStorageCapacity(UUID id) {
        var capacity = repository.getCapacityById(id);
        if (capacity.isEmpty()) {
            throw new IllegalArgumentException("Can't delete non-existing capacity");
        }
        if (capacity.get().isUnloaded()) {
            throw new IllegalArgumentException("Cannot delete a Storage Capacity that has items in it.");
        }
        repository.deleteById(id);
    }

    public Optional<StorageCapacity> findById(UUID id) {
        return repository.getCapacityById(id);
    }

    public List<StorageCapacity> findAll() {
        return repository.findAll();
    }

    public List<StorageCapacity> provideCapacityFor(ItemAmount amount, List<StorageType> storageTypes)
            throws NotEnoughCapacityAvailableException {
        var allsCapacitiesWithMatchingType = repository.findAll().stream()
                .filter(capacity -> storageTypes.stream()
                        .anyMatch(wants -> wants.getUuid().equals(capacity.getStorageTypeId())))
                .collect(Collectors.toList());
        var reservedStorage = ItemAmount.ZERO;
        List<StorageCapacity> neededStorage = new ArrayList<>();
        for (StorageCapacity storageCapacity : allsCapacitiesWithMatchingType) {
            if (reservedStorage.greaterOrEquals(amount)) {
                break;
            }
            reservedStorage = reservedStorage.add(storageCapacity.getRemainingCapacity());
            neededStorage.add(storageCapacity);
        }
        if (!reservedStorage.greaterOrEquals(amount)) {
            throw new NotEnoughCapacityAvailableException();
        }
        return Collections.unmodifiableList(neededStorage);
    }

    public void unloadCapacity(InventoryItemDecreasedEvent event) {
        List<StorageCapacity> capacitiesWithItem = repository.findAll().stream()
                .filter(capacity -> capacity.hasItem(event.storageItemId())).collect(Collectors.toList());
        var remainingUnloadAmount = event.decreasedBy();
        for (StorageCapacity currentCapacity : capacitiesWithItem) {
            var item = currentCapacity.getItem(event.storageItemId());
            if (item.isEmpty()) {
                continue;
            }
            var currentUnloadAmount = item.get().amount().greaterOrEquals(remainingUnloadAmount) ? remainingUnloadAmount
                    : item.get().amount();
            try {
                currentCapacity.unload(event.storageItemId(), currentUnloadAmount, publisher);
            } catch (ItemNotInStoreException | UnloadGreaterThanStoredAmoundException e) {
                // TODO Log the programmers failed us
            }
            remainingUnloadAmount = remainingUnloadAmount.subtractSafe(item.get().amount());
        }
        if (remainingUnloadAmount.greaterOrEquals(ItemAmount.ZERO)) {
            // TODO LOG this is sus! We should have 0 left over after unloading!
        }
    }

}
