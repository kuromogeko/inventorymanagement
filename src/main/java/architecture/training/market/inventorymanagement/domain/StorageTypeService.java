package architecture.training.market.inventorymanagement.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.StorageType;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeUpdateRequest;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeUseCase;

public class StorageTypeService implements StorageTypeUseCase {

    private final DomainEventPublisher publisher;
    private final StorageTypeRepository repository;

    public StorageTypeService(DomainEventPublisher publisher, StorageTypeRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    @Override
    public void updateStorageType(UUID id, StorageTypeUpdateRequest request) {
        var type = repository.findById(id);
        if(type.isEmpty()){
            throw new IllegalArgumentException("No StorageType found with Id");
        }
        type.get().update(request, publisher);
    }

    @Override
    public void deleteStorageType(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<StorageType> getStorageTypes() {
        return repository.findAll();
    }

    @Override
    public Optional<StorageType> getStorageTypeById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public StorageType createStorageType(String name, String description) {
        return StorageType.create(name, description, this.publisher);
    }

}
