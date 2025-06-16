package architecture.training.market.inventorymanagement.application;

import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItem;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemBestBeforeChanged;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemDecreasedEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemRepository;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemStoredEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.CapacityBelowZeroException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Component
public class HashInventoryItemRepositoryImplImpl implements InventoryItemRepository {

    private final HashMap<UUID, ItemHistory> repo;

    public HashInventoryItemRepositoryImplImpl() {
        this.repo = new HashMap<>();
    }

    @Override
    public Optional<InventoryItem> findById(UUID id) {
        var history = Optional.ofNullable(repo.get(id));
        return history.map(record -> {
            var start = InventoryItem.applyCreate(record.fuse());
            for (var event : record.events()) {
                if (event instanceof InventoryItemStoredEvent store) {
                    start.applyInventoryItemStoredEvent(store);
                } else if (event instanceof InventoryItemDecreasedEvent dec) {
                    try {
                        start.applyStockDecrease(dec);
                    } catch (CapacityBelowZeroException e) {
                        System.out.println("More stock removed than allowed in replay of object....");
                    }
                } else if (event instanceof InventoryItemBestBeforeChanged best) {
                    start.applySoonerBestBefore(best);
                }
            }
            return start;
        });
    }

    @Override
    public void saveEvent(InventoryItemBestBeforeChanged event) {
        Optional.ofNullable(repo.get(event.storageItemId())).ifPresent(itemHistory -> itemHistory.events.add(event));
    }

    @Override
    public void saveEvent(InventoryItemStoredEvent event) {
        Optional.ofNullable(repo.get(event.storageItemId())).ifPresentOrElse(itemHistory -> itemHistory.events.add(event),
                () -> repo.put(event.storageItemId(), new ItemHistory(event, new ArrayList<>())));
    }

    @Override
    public void saveEvent(InventoryItemDecreasedEvent event) {
        Optional.ofNullable(repo.get(event.storageItemId())).ifPresent(itemHistory -> itemHistory.events.add(event));
    }

    private record ItemHistory(InventoryItemStoredEvent fuse, ArrayList<InventoryItemEvent> events) {
    }
}
