package architecture.training.market.inventorymanagement;

import architecture.training.market.inventorymanagement.domain.StorageTypeService;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryCount;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryRequest;
import architecture.training.market.inventorymanagement.domain.storage.inventory.InventoryService;
import architecture.training.market.inventorymanagement.domain.storage.items.BestBefore;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItem;
import architecture.training.market.inventorymanagement.domain.storage.items.InventoryItemRepository;
import architecture.training.market.inventorymanagement.domain.storage.items.ItemService;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItemService;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemStore;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacityService;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class InventorymanagementApplicationTests {

    @Autowired
    StorageTypeService storageTypeService;
    @Autowired
    StorageItemService storageItemService;
    @Autowired
    StorageCapacityService storageCapacityService;
    @Autowired
    ItemService itemService;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    InventoryItemRepository itemRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void defaultTest() {
        var defaultType = storageTypeService.createStorageType("default", "default storage type");
        var storageItem = storageItemService.createStorageItem(UUID.randomUUID(), List.of(defaultType));
        var capacity = storageCapacityService.createStorageCapacity("Schrank", defaultType.getUuid(), new ItemAmount(30));
		BestBefore bestBefore = new BestBefore(LocalDate.of(2026, 1, 1));
		var inventoryItem = itemService.storeItem(new StorageRequest(storageItem.getItemId(), new ItemAmount(10), bestBefore, capacity.getId()));
		var updatedCapacity = storageCapacityService.findById(capacity.getId());
		assertEquals(new ItemAmount(10),updatedCapacity.get().getItem(storageItem.getItemId()).get().amount());
		assertEquals(new ItemAmount(10), inventoryItem.getStoredAmount());
		assertEquals(bestBefore, inventoryItem.getBestBefore());
    }

    @Test
    void inventoryUpdateDecreaseTest() {
        var defaultType = storageTypeService.createStorageType("inventory", "default storage type");
        var storageItem = storageItemService.createStorageItem(UUID.randomUUID(), List.of(defaultType));
        var capacity = storageCapacityService.createStorageCapacity("Schrank", defaultType.getUuid(), new ItemAmount(30));
        BestBefore bestBefore = new BestBefore(LocalDate.of(2026, 1, 1));
        BestBefore bestBeforeNew = new BestBefore(LocalDate.of(2025, 1, 1));
        itemService.storeItem(new StorageRequest(storageItem.getItemId(), new ItemAmount(10), bestBefore, capacity.getId()));
        inventoryService.countInventory(new InventoryRequest(List.of(new InventoryCount(storageItem.getItemId(), new ItemAmount(5),bestBeforeNew)),UUID.randomUUID()));
        var updatedCapacity = storageCapacityService.findById(capacity.getId());
        ItemStore updatedStoredItem = updatedCapacity.get().getItem(storageItem.getItemId()).get();
        assertEquals(new ItemAmount(5), updatedStoredItem.amount());
        assertEquals(new ItemAmount(25), updatedCapacity.get().getRemainingCapacity());
        var updatedInventoryItem = itemRepository.findById(storageItem.getItemId()).get();
        assertEquals(new ItemAmount(5), updatedInventoryItem.getStoredAmount());
        assertEquals(bestBeforeNew, updatedInventoryItem.getBestBefore() );
    }

    @Test
    void inventoryUpdateIncreaseTest() {
        var defaultType = storageTypeService.createStorageType("inventory", "default storage type");
        var storageItem = storageItemService.createStorageItem(UUID.randomUUID(), List.of(defaultType));
        var capacity = storageCapacityService.createStorageCapacity("Schrank", defaultType.getUuid(), new ItemAmount(30));
        BestBefore bestBefore = new BestBefore(LocalDate.of(2027, 1, 1));
        BestBefore bestBeforeNew = new BestBefore(LocalDate.of(2026, 1, 1));
        itemService.storeItem(new StorageRequest(storageItem.getItemId(), new ItemAmount(10), bestBefore, capacity.getId()));
        inventoryService.countInventory(new InventoryRequest(List.of(new InventoryCount(storageItem.getItemId(), new ItemAmount(20),bestBeforeNew)),UUID.randomUUID()));
        var updatedCapacity = storageCapacityService.findById(capacity.getId());
        ItemStore updatedStoredItem = updatedCapacity.get().getItem(storageItem.getItemId()).get();
        assertEquals(new ItemAmount(20), updatedStoredItem.amount());
        assertEquals(new ItemAmount(10), updatedCapacity.get().getRemainingCapacity());
        InventoryItem updatedInventoryItem = itemRepository.findById(storageItem.getItemId()).get();
        assertEquals(new ItemAmount(20), updatedInventoryItem.getStoredAmount());
        assertEquals(bestBeforeNew, updatedInventoryItem.getBestBefore() );
    }

    @Test
    void inventoryNewItemFoundTest() {
        var defaultType = storageTypeService.createStorageType("inventory", "default storage type");
        var storageItem = storageItemService.createStorageItem(UUID.randomUUID(), List.of(defaultType));
        var capacity = storageCapacityService.createStorageCapacity("Schrank", defaultType.getUuid(), new ItemAmount(30));
        BestBefore bestBefore = new BestBefore(LocalDate.of(2027, 1, 1));
        inventoryService.countInventory(new InventoryRequest(List.of(new InventoryCount(storageItem.getItemId(), new ItemAmount(20),bestBefore)),UUID.randomUUID()));
        var updatedCapacity = storageCapacityService.findById(capacity.getId());
        ItemStore updatedStoredItem = updatedCapacity.get().getItem(storageItem.getItemId()).get();
        assertEquals(new ItemAmount(20), updatedStoredItem.amount());
        assertEquals(new ItemAmount(10), updatedCapacity.get().getRemainingCapacity());
        InventoryItem updatedInventoryItem = itemRepository.findById(storageItem.getItemId()).get();
        assertEquals(new ItemAmount(20), updatedInventoryItem.getStoredAmount());
        assertEquals(bestBefore, updatedInventoryItem.getBestBefore() );
    }

    @Test
    void deleteCapacityAfterEmptyTest() {
        var defaultType = storageTypeService.createStorageType("inventory", "default storage type");
        var storageItem = storageItemService.createStorageItem(UUID.randomUUID(), List.of(defaultType));
        var capacity = storageCapacityService.createStorageCapacity("Schrank", defaultType.getUuid(), new ItemAmount(30));
        BestBefore bestBefore = new BestBefore(LocalDate.of(2026, 1, 1));
        itemService.storeItem(new StorageRequest(storageItem.getItemId(), new ItemAmount(10), bestBefore, capacity.getId()));
        inventoryService.countInventory(new InventoryRequest(List.of(new InventoryCount(storageItem.getItemId(), new ItemAmount(0),bestBefore)),UUID.randomUUID()));
        var updatedCapacity = storageCapacityService.findById(capacity.getId());
        var updatedStoredItem = updatedCapacity.get().getItem(storageItem.getItemId());
        assertTrue(updatedStoredItem.isEmpty());
        assertEquals(new ItemAmount(30), updatedCapacity.get().getRemainingCapacity());

        storageCapacityService.deleteStorageCapacity(capacity.getId());
        var deletedCapacity = storageCapacityService.findById(capacity.getId());
        assertTrue(deletedCapacity.isEmpty());

    }
}
