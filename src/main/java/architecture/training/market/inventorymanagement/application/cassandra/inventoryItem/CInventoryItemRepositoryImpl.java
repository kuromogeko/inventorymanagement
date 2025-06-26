package architecture.training.market.inventorymanagement.application.cassandra.inventoryItem;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItem;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemBestBeforeChanged;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemDecreasedEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemRepository;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemStoredEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.CapacityBelowZeroException;
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
public class CInventoryItemRepositoryImpl implements InventoryItemRepository {

    public static final String ITEM_DECREASED = "ItemDecreased";
    public static final String ITEM_STORED = "ItemStored";
    public static final String BEST_BEFORE_UPDATE = "BestBeforeChanged";
    private final CassandraInventoryItemRepo repository;
    private final ObjectMapper mapper;

    public CInventoryItemRepositoryImpl(CassandraInventoryItemRepo repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public Optional<InventoryItem> findById(UUID id) {
        var events = repository.findAllById_Id(id.toString());
        if (events.isEmpty()) {
            return Optional.empty();
        }
        return mapEventChainToInventoryItem(events);
    }

    @Override
    public void saveEvent(InventoryItemBestBeforeChanged event) {
        try {
            var entity = new InventoryItemEventEntity(new EventPrimaryKey(event.storageItemId().toString(), Instant.now()), mapper.writeValueAsString(event), BEST_BEFORE_UPDATE);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveEvent(InventoryItemStoredEvent event) {
        try {
            var entity = new InventoryItemEventEntity(new EventPrimaryKey(event.storageItemId().toString(), Instant.now()), mapper.writeValueAsString(event), ITEM_STORED);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveEvent(InventoryItemDecreasedEvent event) {
        try {
            var entity = new InventoryItemEventEntity(new EventPrimaryKey(event.storageItemId().toString(), Instant.now()), mapper.writeValueAsString(event), ITEM_DECREASED);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private Optional<InventoryItem> mapEventChainToInventoryItem(List<InventoryItemEventEntity> events) {
        LinkedList<DomainEvent> queueOfEvents = events.stream()
                .sorted(Comparator.comparing(event -> event.getId().getTimestamp()))
                .map(this::toDomainEvent)
                .collect(Collectors.toCollection(LinkedList::new));
        var fuse = queueOfEvents.poll();
        if (!(fuse instanceof InventoryItemStoredEvent)) {
            throw new RuntimeException("WTF");
        }
        var inventoryItem = InventoryItem.applyCreate((InventoryItemStoredEvent) fuse);
        for (var event : queueOfEvents) {
            //Java XD
            if (event instanceof InventoryItemDecreasedEvent) {
                try {
                    inventoryItem.applyStockDecrease((InventoryItemDecreasedEvent) event);
                } catch (CapacityBelowZeroException e) {
                    System.out.println("More stock removed than allowed in replay of object....");
                }
            } else if (event instanceof InventoryItemStoredEvent) {
                inventoryItem.applyInventoryItemStoredEvent((InventoryItemStoredEvent) event);
            } else if (event instanceof InventoryItemBestBeforeChanged) {
                inventoryItem.applySoonerBestBefore((InventoryItemBestBeforeChanged) event);
            }
        }
        return Optional.of(inventoryItem);
    }

    private DomainEvent toDomainEvent(InventoryItemEventEntity event) {
        try {
            return switch (event.getEventType()) {
                case BEST_BEFORE_UPDATE -> mapper.readValue(event.getEvent(), InventoryItemBestBeforeChanged.class);
                case ITEM_STORED -> mapper.readValue(event.getEvent(), InventoryItemStoredEvent.class);
                case ITEM_DECREASED -> mapper.readValue(event.getEvent(), InventoryItemDecreasedEvent.class);
                default -> throw new RuntimeException("Oof");
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Oof");
        }
    }
}
