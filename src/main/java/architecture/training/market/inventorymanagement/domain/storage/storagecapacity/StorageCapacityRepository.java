package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//package private is intended
interface StorageCapacityRepository {
    Optional<StorageCapacity> getCapacityById(UUID id);
    void deleteById(UUID id);
    List<StorageCapacity> findAll();
}
