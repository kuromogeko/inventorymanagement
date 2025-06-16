package architecture.training.market.inventorymanagement.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.StorageType;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeCreatedEvent;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeUpdatedEvent;

public interface StorageTypeRepository {

    List<StorageType> findAll();
    Optional<StorageType> findById(UUID id);
    void deleteById(UUID id);

    void save(StorageTypeCreatedEvent event);

    void save(StorageTypeUpdatedEvent event);
}
