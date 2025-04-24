package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

public class CapacityBelowZeroException extends Exception {
    public CapacityBelowZeroException(String message) {
        super(message);
    }
}

