package architecture.training.market.inventorymanagement.domain.storage;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Admin
 */
public interface StorageTypeUseCase {
    void createStorageType(StorageType storageType);
    void updateStorageType(UUID id, StorageType storageType);
    void deleteStorageType(UUID id);
    List<StorageType> getStorageTypes();
    StorageType getStorageTypeById(UUID id);
}