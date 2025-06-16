package architecture.training.market.inventorymanagement.domain.storage;

import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;

public class StorageType {

    private UUID uuid;
    private String name;
    private String description;

    private StorageType() {
    }

    public static StorageType create(String name, String description, DomainEventPublisher eventPublisher) {
        var event = new StorageTypeCreatedEvent(UUID.randomUUID(), name, description);
        var storageType = applyCreate(event);
        eventPublisher.publishEvent(event);
        return storageType;
    }

    public static StorageType applyCreate(StorageTypeCreatedEvent event) {
        if (event.name() == null || event.name().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (event.description() == null || event.description().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        var storageType = new StorageType();
        storageType.name = event.name();
        storageType.description = event.description();
        storageType.uuid = event.uuid();
        return storageType;
    }

    public void applyUpdate(StorageTypeUpdatedEvent event){
        this.description = event.description();
        this.name = event.name();
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

    public void update(StorageTypeUpdateRequest request, DomainEventPublisher publisher) {
        this.name = request.name();
        this.description = request.description();
        publisher.publishEvent(new StorageTypeUpdatedEvent(this.uuid, this.description, this.name));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof StorageType))
            return false;
        StorageType that = (StorageType) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
