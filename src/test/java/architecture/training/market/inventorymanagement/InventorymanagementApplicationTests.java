package architecture.training.market.inventorymanagement;

import architecture.training.market.inventorymanagement.domain.StorageTypeService;
import architecture.training.market.inventorymanagement.domain.storage.items.BestBefore;
import architecture.training.market.inventorymanagement.domain.storage.items.ItemService;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItemService;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageCapacityService;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.StorageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class InventorymanagementApplicationTests {

    @Autowired
    StorageTypeService storageTypeService;
    @Autowired
    StorageItemService storageItemService;
    @Autowired
    StorageCapacityService storageCapacityService;
    @Autowired
    ItemService service;

    @Test
    void contextLoads() {
    }

    @Test
    void defaultTest() {
        var defaultType = storageTypeService.createStorageType("default", "default storage type");
        var storageItem = storageItemService.createStorageItem(UUID.randomUUID(), List.of(defaultType));
        var capacity = storageCapacityService.createStorageCapacity("Schrank", defaultType.getUuid(), new ItemAmount(30));
		BestBefore bestBefore = new BestBefore(LocalDate.of(2026, 1, 1));
		var inventoryItem = service.storeItem(new StorageRequest(storageItem.getItemId(), new ItemAmount(10), bestBefore, capacity.getId()));
		var updatedCapacity = storageCapacityService.findById(capacity.getId());
		assertEquals(new ItemAmount(10),updatedCapacity.get().getItem(storageItem.getItemId()).get().amount());
		assertEquals(new ItemAmount(10), inventoryItem.getStoredAmount());
		assertEquals(bestBefore, inventoryItem.getBestBefore());
    }
}
