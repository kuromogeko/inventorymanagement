package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

public record ItemAmount(int capacity)  {


    public ItemAmount {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be non-negative");
        }
    }


    public ItemAmount add(ItemAmount other) {
        return new ItemAmount(this.capacity + other.capacity);
    }

    public ItemAmount subtract(ItemAmount other) throws CapacityBelowZeroException {
        if (this.capacity - other.capacity < 0) {
            throw new CapacityBelowZeroException("Capacity would end up below zero");
        }
        return new ItemAmount(this.capacity - other.capacity);
    }
}
