package architecture.training.market.inventorymanagement.domain.storage.capacity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.ItemAmount;
import architecture.training.market.inventorymanagement.domain.storage.storagecapacity.CapacityBelowZeroException;

public class ItemAmountTest {

    @Test
    void testAdd() {
        ItemAmount capacity = new ItemAmount(10);
        var newCap = capacity.add(new ItemAmount(5));
        assertEquals(15, newCap.capacity());
    }

    @Test
    void testSubtract() throws CapacityBelowZeroException {
        ItemAmount capacity = new ItemAmount(10);
        var newCap = capacity.subtract(new ItemAmount(5));
        assertEquals(5, newCap.capacity());
    }

    @Test
    void testSubtractBelowZero() {
        ItemAmount capacity = new ItemAmount(5);
        assertThrows(CapacityBelowZeroException.class, () -> {
            try {
                capacity.subtract(new ItemAmount(6));
            } catch (CapacityBelowZeroException ex) {
                throw ex;
            }
        });
    }

    @Test
    void testCreateBelowZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ItemAmount(-5);
        });
    }

    @Test
    void testSubtractExactlyZero() throws CapacityBelowZeroException {
        ItemAmount capacity = new ItemAmount(5);
        var newCap = capacity.subtract(new ItemAmount(5));
        assertEquals(0, newCap.capacity());
    }
}
