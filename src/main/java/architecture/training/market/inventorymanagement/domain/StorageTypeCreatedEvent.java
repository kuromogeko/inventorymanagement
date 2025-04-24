package architecture.training.market.inventorymanagement.domain;

import java.util.UUID;

public record StorageTypeCreatedEvent(UUID uuid, String name, String description) implements DomainEvent {

}
