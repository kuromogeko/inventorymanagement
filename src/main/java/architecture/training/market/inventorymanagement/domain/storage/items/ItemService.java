package architecture.training.market.inventorymanagement.domain.storage.items;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import architecture.training.market.inventorymanagement.domain.DomainLogger;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryCount;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryCountedEvent;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.NotEnoughCapacityAvailableException;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.SimpleStorageRequest;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacityService;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageRequest;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ItemService {
    private final StorageCapacityService storageCapacityService;
    private final StorageItemRepository storageItemInformationRepository;
    private final DomainEventPublisher publisher;
    private final DomainLogger logger;
    private final InventoryItemRepository itemRepository;

    public ItemService(StorageCapacityService storageCapacityService, StorageItemRepository storageItemRepository,
                       DomainEventPublisher publisher, InventoryItemRepository itemRepository, DomainLogger logger) {
        this.storageCapacityService = storageCapacityService;
        this.storageItemInformationRepository = storageItemRepository;
        this.publisher = publisher;
        this.logger = logger;
        this.itemRepository = itemRepository;
    }

    public InventoryItem storeItem(StorageRequest request) {
        var storageCapacity = storageCapacityService.findById(request.storageCapacityId());
        if (storageCapacity.isEmpty()) {
            throw new IllegalArgumentException("Storage capacity not found for ID: " + request.storageCapacityId());
        }
        var storageItem = storageItemInformationRepository.findById(request.storageItemId());
        if (storageItem.isEmpty()) {
            throw new IllegalArgumentException("Storage item not found for ID: " + request.storageItemId());
        }
        if (!storageCapacity.get().canStore(storageItem.get())) {
            throw new IllegalArgumentException(
                    "Storage capacity cannot store the specified item: " + request.storageItemId());
        }
        // we don't need to check for existing items since this is an aggregating view
        // and new stores will just be added
        return InventoryItem.create(request.storageItemId(), request.amount(), request.bestBefore(),
                storageCapacity.get(), publisher);
    }

    public void storeItemInAnyCapacities(SimpleStorageRequest request) throws NotEnoughCapacityAvailableException {
        var storageItem = storageItemInformationRepository.findById(request.storageItemId());
        if (storageItem.isEmpty()) {
            throw new IllegalArgumentException("Storage item not found for ID: " + request.storageItemId());
        }

        var neededCapacities = storageCapacityService.provideCapacityFor(request.amount(),
                storageItem.get().getStorageTypes());
        var remainingAmount = request.amount();
        for (var capacity : neededCapacities) {
            if (capacity.getRemainingCapacity().greaterOrEquals(remainingAmount)) {
                this.storeItem(new StorageRequest(request.storageItemId(), remainingAmount, request.bestBefore(),
                        capacity.getId()));
                remainingAmount = ItemAmount.ZERO;
                break;
            }
            var itemCapacityOfUnit = capacity.getRemainingCapacity();
            this.storeItem(new StorageRequest(request.storageItemId(), itemCapacityOfUnit, request.bestBefore(),
                    capacity.getId()));
            remainingAmount = remainingAmount.subtractSafe(itemCapacityOfUnit);
        }
    }

    @EventListener
    public void handleInventoryCountedEvent(InventoryCountedEvent event) {
        event.itemCounts().stream().forEach(count -> {
            var item = itemRepository.findById(count.storageItemId());
            if (item.isEmpty()) {
                logger.log("Counted Item was not stored before!");
                try {
                    this.storeItemInAnyCapacities(new SimpleStorageRequest(count.storageItemId(), count.countedAmount(),
                            count.soonestBestBefore()));
                } catch (NotEnoughCapacityAvailableException e) {
                    // Realisticly speaking counting things we don't have capacity for makes no
                    // sense however....
                    logger.log("Counted Item (not stored before) has no capacity to be stored");
                }
                return;
            }
            if (item.get().getStoredAmount().greaterThan(count.countedAmount())) {
                handleInventoryItemDecrease(count, item.get());
            } else if (count.countedAmount().greaterThan(item.get().getStoredAmount())) {
                handleInventoryItemIncrease(count, item);
            }
        });
    }

    public void removeItem(UUID storageItemId, ItemAmount amount) {
        var item = itemRepository.findById(storageItemId)
                .orElseThrow(() -> new IllegalArgumentException("Storage item not found for ID: " + storageItemId));
        if (!item.getStoredAmount().greaterOrEquals(amount)) {
            throw new IllegalArgumentException("Not enough stock to remove");
        }
        item.stockDecrease(amount, publisher);
    }

    private void handleInventoryItemDecrease(InventoryCount count, InventoryItem item) {
        item.stockDecrease(
                item.getStoredAmount().subtractSafe(count.countedAmount()), publisher);
        if (count.soonestBestBefore().isOlder(item.getBestBefore())) {
            item.soonerBestBeforeFound(count.soonestBestBefore(), publisher);
        }
    }

    private void handleInventoryItemIncrease(InventoryCount count, Optional<InventoryItem> item) {
        try {
            this.storeItemInAnyCapacities(new SimpleStorageRequest(count.storageItemId(),
                    count.countedAmount().subtractSafe(item.get().getStoredAmount()),
                    count.soonestBestBefore()));
        } catch (NotEnoughCapacityAvailableException e) {
            // Realisticly speaking counting things we don't have capacity for makes no
            // sense however....
            logger.log("Counted Item (count was higher than store) has no capacity to be stored");
        }
    }

    @EventListener
    public void handleItemBestBeforeChange(InventoryItemBestBeforeChanged event){
        itemRepository.saveEvent(event);
    }

    @EventListener
    public void handleInventoryItemStoredEvent(InventoryItemStoredEvent event){
        itemRepository.saveEvent(event);
    }

    @EventListener
    public void handleItemDecreaseEvent(InventoryItemDecreasedEvent event){
        itemRepository.saveEvent(event);
    }
}
