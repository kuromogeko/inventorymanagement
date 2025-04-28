package architecture.training.market.inventorymanagement.domain.storage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author Admin
 */
public interface StorageTypeUseCase {
    StorageType createStorageType(String name, String description);
    void updateStorageType(UUID id, StorageTypeUpdateRequest request);
    void deleteStorageType(UUID id);
    List<StorageType> getStorageTypes();
    Optional<StorageType> getStorageTypeById(UUID id);
}