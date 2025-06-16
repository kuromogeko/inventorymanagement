package architecture.training.market.inventorymanagement.domain.storage.inventory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import org.springframework.context.event.EventListener;

public class Inventory {
    private UUID id;
    private List<InventoryCount> itemCounts;
    private UUID employeeId;

    private Inventory() {
    }

    public static Inventory create(List<InventoryCount> itemCounts, UUID employeeId,
            DomainEventPublisher publisher) {
        var ev = new InventoryCountedEvent(UUID.randomUUID(), itemCounts, employeeId);
        var inventory = applyCreate(ev);
        publisher.publishEvent(ev);
        return inventory;
    }

    public static Inventory applyCreate(InventoryCountedEvent ev) {
        if (ev.id() == null || ev.employeeId() == null || ev.itemCounts() == null
                || ev.itemCounts().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Event must have non-null id, employeeId and itemCounts with no null elements.");
        }
        var inventory = new Inventory();
        inventory.id = ev.id();
        inventory.employeeId = ev.employeeId();
        inventory.itemCounts = ev.itemCounts();
        return inventory;
    }
}
