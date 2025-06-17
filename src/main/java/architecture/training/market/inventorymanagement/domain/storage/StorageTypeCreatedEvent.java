package architecture.training.market.inventorymanagement.domain.storage;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record StorageTypeCreatedEvent(UUID uuid, String name, String description) implements DomainEvent {
    @Override
    public String toString() {
        return "StorageTypeCreatedEvent{" +
               "uuid=" + uuid +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
