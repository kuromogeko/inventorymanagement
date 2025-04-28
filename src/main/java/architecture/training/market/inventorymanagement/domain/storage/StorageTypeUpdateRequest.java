package architecture.training.market.inventorymanagement.domain.storage;

public record StorageTypeUpdateRequest(String name, String description)  {

    public StorageTypeUpdateRequest {
        if (name == null || description == null) {
            throw new IllegalArgumentException("Name and description cannot be null");
        }
        if (name.isEmpty() || description.isEmpty()) {
            throw new IllegalArgumentException("Name and description cannot be empty");
        }
    }

}
