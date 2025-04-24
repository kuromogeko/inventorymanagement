package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class StorageCapacityTest {

    @Test
    public void testStorageCapacityArguments(){
        assertThrows(IllegalArgumentException.class,() -> {
            StorageCapacity.applyCreate(new StorageCapacityCreatedEvent(null, "Name", UUID.randomUUID(), new ItemAmount(5)));
        } );
    }

    @Test
    public void testStorageCapacityNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            StorageCapacity.applyCreate(new StorageCapacityCreatedEvent(UUID.randomUUID(), null, UUID.randomUUID(), new ItemAmount(5)));
        });
    }

    @Test
    public void testStorageCapacityNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            StorageCapacity.applyCreate(new StorageCapacityCreatedEvent(UUID.randomUUID(), "", UUID.randomUUID(), new ItemAmount(5)));
        });
    }

    @Test
    public void testStorageCapacityIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            StorageCapacity.applyCreate(new StorageCapacityCreatedEvent(UUID.randomUUID(), "Name", null, new ItemAmount(5)));
        });
    }

    @Test
    public void testStorageCapacityCapacityIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            StorageCapacity.applyCreate(new StorageCapacityCreatedEvent(UUID.randomUUID(), "Name", UUID.randomUUID(), null));
        });
    }
}
