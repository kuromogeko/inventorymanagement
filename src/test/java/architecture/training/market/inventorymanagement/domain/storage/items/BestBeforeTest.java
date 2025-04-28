package architecture.training.market.inventorymanagement.domain.storage.items;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class BestBeforeTest {
    @Test
    void testOlder() {
        var bestOld = new BestBefore(LocalDate.of(2020, 1, 1));
        var bestNew = new BestBefore(LocalDate.of(2020, 1, 2));
        assertEquals(bestOld, bestOld.older(bestNew));
        assertEquals(bestOld, bestNew.older(bestOld));
    }
}
