package architecture.training.market.inventorymanagement.domain.storage.items;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.CapacityBelowZeroException;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacity;

public class InventoryItem {
    private BestBefore bestBefore;
    private ItemAmount amount;
    private UUID storageItemId;

    private InventoryItem() {

    }

    public static InventoryItem create(UUID storageItemId, ItemAmount amount, BestBefore bestBefore,
            StorageCapacity store,
            DomainEventPublisher publisher) {
        var ev = new InventoryItemStoredEvent(storageItemId, store.getId(), amount, bestBefore);
        var item = applyCreate(ev);
        publisher.publishEvent(ev);
        return item;
    }

    public static InventoryItem applyCreate(InventoryItemStoredEvent ev) {
        if (ev.storageItemId() == null || ev.storageItemId().toString().isEmpty() || ev.amount() == null
                || ev.bestBefore() == null || ev.storageCapacityId() == null) {
            throw new IllegalArgumentException(
                    "storageItemId, storageCapacityId, amount, or bestBefore cannot be null");
        }
        var item = new InventoryItem();
        item.bestBefore = ev.bestBefore();
        item.amount = ev.amount();
        item.storageItemId = ev.storageItemId();
        return item;
    }

    public ItemAmount getStoredAmount() {
        return this.amount;
    }

    public BestBefore getBestBefore() {
        return this.bestBefore;
    }

    public void applyInventoryItemStoredEvent(InventoryItemStoredEvent event) {
        this.bestBefore = this.bestBefore.older(event.bestBefore());
        this.amount = this.amount.add(event.amount());
    }

    public void stockDecrease(ItemAmount difference, DomainEventPublisher publisher) {
        try {
            InventoryItemDecreasedEvent event = new InventoryItemDecreasedEvent(this.storageItemId, difference);
            applyStockDecrease(event);
            publisher.publishEvent(event);
        } catch (CapacityBelowZeroException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void applyStockDecrease(InventoryItemDecreasedEvent event) throws CapacityBelowZeroException {
        this.amount = this.amount.subtract(event.decreasedBy());
    }

    public void soonerBestBeforeFound(BestBefore soonestBestBefore, DomainEventPublisher publisher) {
        var event = new InventoryItemBestBeforeChanged(this.storageItemId, soonestBestBefore);
        this.applySoonerBestBefore(event);
        publisher.publishEvent(event);
    }

    public void applySoonerBestBefore(InventoryItemBestBeforeChanged event) {
        this.bestBefore = this.bestBefore.older(event.bestBefore());
    }
}
