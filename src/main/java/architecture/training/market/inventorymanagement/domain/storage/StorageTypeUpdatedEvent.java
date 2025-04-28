package architecture.training.market.inventorymanagement.domain.storage;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEvent;

public record StorageTypeUpdatedEvent(UUID id, String description, String name) implements DomainEvent {

}
