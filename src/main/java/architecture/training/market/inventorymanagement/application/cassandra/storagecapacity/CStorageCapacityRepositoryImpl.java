package architecture.training.market.inventorymanagement.application.cassandra.storagecapacity;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemStoredEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemUnloadedEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacity;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacityCreatedEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacityRepository;
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
public class CStorageCapacityRepositoryImpl implements StorageCapacityRepository {

    public static final String CREATED_EVENT = "CreatedEvent";
    public static final String ITEM_UNLOAD = "ItemUnloaded";
    public static final String ITEM_STORED = "ItemStored";
    private final CassandraStorageCapacityRepo repository;
    private final ObjectMapper mapper;

    public CStorageCapacityRepositoryImpl(CassandraStorageCapacityRepo repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public void save(InventoryItemStoredEvent event) {
        try {
            var entity = new StorageCapacityEventEntity(new EventPrimaryKey(event.storageCapacityId().toString(), Instant.now()), mapper.writeValueAsString(event), ITEM_STORED);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(ItemUnloadedEvent event) {
        try {
            var entity = new StorageCapacityEventEntity(new EventPrimaryKey(event.storageCapacityId().toString(), Instant.now()), mapper.writeValueAsString(event), ITEM_UNLOAD);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<StorageCapacity> getCapacityById(UUID id) {
        var events = repository.findAllById_Id(id.toString());
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return mapEventChainToStorageCapacity(events);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteAllById_Id(id.toString());
    }

    @Override
    public List<StorageCapacity> findAll() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(event -> event.getId().getId()))
                .values().stream()
                .map(this::mapEventChainToStorageCapacity)
                .filter(Optional::isPresent)
                .map(Optional::get).toList();
    }

    @Override
    public void save(StorageCapacityCreatedEvent event) {
        try {
            var entity = new StorageCapacityEventEntity(new EventPrimaryKey(event.id().toString(), Instant.now()), mapper.writeValueAsString(event), CREATED_EVENT);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private Optional<StorageCapacity> mapEventChainToStorageCapacity(List<StorageCapacityEventEntity> events) {
        LinkedList<DomainEvent> queueOfEvents = events.stream()
                .sorted(Comparator.comparing(event -> event.getId().getTimestamp()))
                .map(this::toDomainEvent)
                .collect(Collectors.toCollection(LinkedList::new));
        var fuse = queueOfEvents.poll();
        if (!(fuse instanceof StorageCapacityCreatedEvent)) {
            throw new RuntimeException("WTF");
        }
        var storageCapacity = StorageCapacity.applyCreate((StorageCapacityCreatedEvent) fuse);
        for (var event : queueOfEvents) {
            //Java XD
            if (event instanceof ItemUnloadedEvent) {
                storageCapacity.applyUnload((ItemUnloadedEvent) event);
            } else if (event instanceof InventoryItemStoredEvent) {
                storageCapacity.applyStore((InventoryItemStoredEvent) event);
            }
        }
        return Optional.of(storageCapacity);
    }

    private DomainEvent toDomainEvent(StorageCapacityEventEntity event) {
        try {
            return switch (event.getEventType()) {
                case CREATED_EVENT -> mapper.readValue(event.getEvent(), StorageCapacityCreatedEvent.class);
                case ITEM_STORED -> mapper.readValue(event.getEvent(), InventoryItemStoredEvent.class);
                case ITEM_UNLOAD -> mapper.readValue(event.getEvent(), ItemUnloadedEvent.class);
                default -> throw new RuntimeException("Oof");
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Oof");
        }
    }
}
