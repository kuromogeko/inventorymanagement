package architecture.training.market.inventorymanagement.application.cassandra.inventory;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import architecture.training.market.inventorymanagement.domain.storage.inventory.Inventory;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryCountedEvent;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryRepository;
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
public class CInventoryRepositoryImpl implements InventoryRepository {

    public static final String COUNTED_EVENT = "CountedEvent";
    private final CassandraInventoryRepo repository;
    private final ObjectMapper mapper;

    public CInventoryRepositoryImpl(CassandraInventoryRepo repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public Optional<Inventory> findById(UUID id) {
        var events = repository.findAllById_Id(id.toString());
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return mapEventChainToInventory(events);
    }

    @Override
    public void saveEvent(InventoryCountedEvent event) {
        try {
            var entity = new InventoryEventEntity(new EventPrimaryKey(event.id().toString(), Instant.now()), mapper.writeValueAsString(event), COUNTED_EVENT);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private Optional<Inventory> mapEventChainToInventory(List<InventoryEventEntity> events) {
        LinkedList<InventoryCountedEvent> queueOfEvents = events.stream()
                .sorted(Comparator.comparing(event -> event.getId().getTimestamp()))
                .map(this::toDomainEvent)
                .collect(Collectors.toCollection(LinkedList::new));
        var fuse = queueOfEvents.poll();
        var storageType = Inventory.applyCreate(fuse);
        return Optional.of(storageType);
    }

    private InventoryCountedEvent toDomainEvent(InventoryEventEntity event) {
        try {
            return switch (event.getEventType()) {
                //Java XD
                case COUNTED_EVENT -> mapper.readValue(event.getEvent(), InventoryCountedEvent.class);
                default -> throw new RuntimeException("Oof");
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Oof");
        }
    }
}
