package architecture.training.market.inventorymanagement.application.cassandra.storageitem;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItem;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItemCreatedEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItemRepository;
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
public class CStorageItemRepositoryImpl implements StorageItemRepository {

    public static final String CREATED_EVENT = "CreatedEvent";
    private final CassandraStorageItemRepo repository;
    private final ObjectMapper mapper;

    public CStorageItemRepositoryImpl(CassandraStorageItemRepo repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public Optional<StorageItem> findById(UUID id) {
        var events = repository.findAllById_Id(id.toString());
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return mapEventChainToStorageType(events);
    }

    @Override
    public void save(StorageItemCreatedEvent event) {
        try {
            var entity = new StorageItemEventEntity(new EventPrimaryKey(event.itemId().toString(), Instant.now()), mapper.writeValueAsString(event), CREATED_EVENT);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private Optional<StorageItem> mapEventChainToStorageType(List<StorageItemEventEntity> events) {
        LinkedList<StorageItemCreatedEvent> queueOfEvents = events.stream()
                .sorted(Comparator.comparing(event -> event.getId().getTimestamp()))
                .map(this::toDomainEvent)
                .collect(Collectors.toCollection(LinkedList::new));
        var fuse = queueOfEvents.poll();
        var storageType = StorageItem.applyCreate(fuse);
        return Optional.of(storageType);
    }

    private StorageItemCreatedEvent toDomainEvent(StorageItemEventEntity event) {
        try {
            return switch (event.getEventType()) {
                //Java XD
                case CREATED_EVENT -> mapper.readValue(event.getEvent(), StorageItemCreatedEvent.class);
                default -> throw new RuntimeException("Oof");
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Oof");
        }
    }
}
