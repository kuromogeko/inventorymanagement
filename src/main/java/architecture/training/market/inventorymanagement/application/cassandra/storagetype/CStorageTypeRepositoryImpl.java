package architecture.training.market.inventorymanagement.application.cassandra.storagetype;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.StorageTypeRepository;
import architecture.training.market.inventorymanagement.domain.storage.StorageType;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeCreatedEvent;
import architecture.training.market.inventorymanagement.domain.storage.StorageTypeUpdatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CStorageTypeRepositoryImpl implements StorageTypeRepository {

    public static final String CREATED_EVENT = "CreatedEvent";
    public static final String UPDATED_EVENT = "UpdatedEvent";
    private final CassandraStorageRepo repository;
    private final ObjectMapper mapper;

    public CStorageTypeRepositoryImpl(CassandraStorageRepo repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<StorageType> findAll() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(event -> event.getId().getId()))
                .values().stream()
                .map(this::mapEventChainToStorageType)
                .filter(Optional::isPresent)
                .map(Optional::get).toList();
    }

    @Override
    public Optional<StorageType> findById(UUID id) {
        var events = repository.findAllById_Id(id.toString());
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return mapEventChainToStorageType(events);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteAllById_Id(id.toString());
    }

    @Override
    public void save(StorageTypeCreatedEvent event) {
        try {
            var entity = new StorageTypeEventEntity(new EventPrimaryKey(event.uuid().toString(), Instant.now()), mapper.writeValueAsString(event), CREATED_EVENT);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(StorageTypeUpdatedEvent event) {
        try {
            var entity = new StorageTypeEventEntity(new EventPrimaryKey(event.id().toString(), Instant.now()), mapper.writeValueAsString(event), UPDATED_EVENT);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<StorageType> mapEventChainToStorageType(List<StorageTypeEventEntity> events) {
        LinkedList<DomainEvent> queueOfEvents = events.stream()
                .sorted(Comparator.comparing(event -> event.getId().getTimestamp()))
                .map(this::toDomainEvent)
                .collect(Collectors.toCollection(LinkedList::new));
        var fuse = queueOfEvents.poll();
        if (!(fuse instanceof StorageTypeCreatedEvent)) {
            throw new RuntimeException("WTF");
        }
        var storageType = StorageType.applyCreate((StorageTypeCreatedEvent) fuse);
        for (var event : queueOfEvents) {
            if (event instanceof StorageTypeUpdatedEvent) {
                storageType.applyUpdate((StorageTypeUpdatedEvent) event);
            }
        }
        return Optional.of(storageType);
    }

    private DomainEvent toDomainEvent(StorageTypeEventEntity event) {
        try {
            return switch (event.getEventType()) {
                //Java XD
                case CREATED_EVENT -> mapper.readValue(event.getEvent(), StorageTypeCreatedEvent.class);
                case UPDATED_EVENT -> mapper.readValue(event.getEvent(), StorageTypeUpdatedEvent.class);
                default -> throw new RuntimeException("Oof");
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Oof");
        }
    }
}
