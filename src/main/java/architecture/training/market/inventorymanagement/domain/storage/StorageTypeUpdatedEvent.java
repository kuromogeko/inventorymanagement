package architecture.training.market.inventorymanagement.domain.storage;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record StorageTypeUpdatedEvent(UUID id, String description, String name) implements DomainEvent {

    @Override
    public String toString() {
        return "StorageTypeUpdatedEvent{" +
               "id=" + id +
               ", description='" + description + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
