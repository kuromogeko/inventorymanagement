package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

public record ItemAmount(int capacity)  {

    public static final ItemAmount ZERO = ItemAmount.ZERO;

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

    public boolean fits(ItemAmount other){
        return this.capacity - other.capacity >= 0;
    }

    /**
     * Subtraction for use in contexts where the result can not go below 0, does not throw an exception
     * In case of wrong usage (e.g. 7 - 12) will return 0;
     * @param other /
     * @return Subtraction result of this - other
     */
    public ItemAmount subtractSafe(ItemAmount other){
        if (this.capacity - other.capacity < 0) {
            return ItemAmount.ZERO;
        }
        return new ItemAmount(this.capacity - other.capacity);
    }

    public boolean greaterOrEquals(ItemAmount other){
        return this.capacity >= other.capacity;
    }


    public boolean greaterThan(ItemAmount other) {
        return this.capacity > other.capacity;
    }
}
