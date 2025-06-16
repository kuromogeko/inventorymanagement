package architecture.training.market.inventorymanagement.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemStoredEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemUnloadedEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacity;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacityCreatedEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacityRepository;

public class HashStorageCapacityRepositoryImpl implements StorageCapacityRepository{
    
    private final HashMap<UUID, StorageHistory> repo;

    public HashStorageCapacityRepositoryImpl() {
        this.repo = new HashMap<>();
    }

    @Override
    public Optional<StorageCapacity> getCapacityById(UUID id) {
        return Optional.ofNullable(this.repo.get(id)).map(this::restore);
    }

    @Override
    public void deleteById(UUID id) {
        this.repo.remove(id);
    }

    @Override
    public List<StorageCapacity> findAll() {
        return this.repo.values().stream().map(this::restore).collect(Collectors.toList());
    }

    private StorageCapacity restore(StorageHistory history){
        var cap = StorageCapacity.applyCreate(history.fuse());
        for(var event: history.events()){
            if(event instanceof InventoryItemStoredEvent store){
                cap.applyStore(store);
            }else if(event instanceof ItemUnloadedEvent unload){
                cap.applyUnload(unload);
            }
        }
        return cap;
    }

    private record StorageHistory(StorageCapacityCreatedEvent fuse, ArrayList<DomainEvent> events) {
    }
    
}
