package architecture.training.market.inventorymanagement.domain;

import java.util.UUID;

class StorageType {

    private UUID uuid;
    private String name;
    private String description;

    public StorageType(String name, String description, DomainEventPublisher eventPublisher) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.name = name;
        this.description = description;
        this.uuid = UUID.randomUUID();
        eventPublisher.publishEvent(new StorageTypeCreatedEvent(uuid, name, description));
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
