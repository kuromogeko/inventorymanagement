package architecture.training.market.inventorymanagement.application;

import architecture.training.market.inventorymanagement.domain.StorageTypeRepository;
import architecture.training.market.inventorymanagement.domain.storage.StorageType;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeCreatedEvent;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class HashStorageTypeRepositoryImpl implements StorageTypeRepository {
    private static final Logger logger = LoggerFactory.getLogger(HashStorageTypeRepositoryImpl.class);

    private final HashMap<UUID, StorageHistory> repo;

    public HashStorageTypeRepositoryImpl() {
        this.repo = new HashMap<>();
    }

    private static StorageType replay(StorageHistory record) {
        var start = StorageType.applyCreate(record.fuse());
        for (var event : record.events()) {
            start.applyUpdate(event);
        }
        return start;
    }

    @Override
    public List<StorageType> findAll() {
        return repo.values().stream().map(HashStorageTypeRepositoryImpl::replay).toList();
    }

    @Override
    public Optional<StorageType> findById(UUID id) {
        var history = Optional.ofNullable(repo.get(id));
        return history.map(HashStorageTypeRepositoryImpl::replay);
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void save(StorageTypeCreatedEvent event) {
        repo.put(event.uuid(), new StorageHistory(event, new ArrayList<>()));
    }

    @Override
    public void save(StorageTypeUpdatedEvent event) {
        Optional.ofNullable(repo.get(event.id())).ifPresentOrElse(storageHistory -> storageHistory.events.add(event),
                () -> logger.error("StorageType updated but not created. id: {}", event.id()));
    }

    private record StorageHistory(StorageTypeCreatedEvent fuse, ArrayList<StorageTypeUpdatedEvent> events) {
    }
}
