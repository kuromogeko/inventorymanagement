package architecture.training.market.inventorymanagement.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.StorageType;

public interface StorageTypeRepository {

    List<StorageType> findAll();
    Optional<StorageType> findById(UUID id);
    void deleteById(UUID id);
}
