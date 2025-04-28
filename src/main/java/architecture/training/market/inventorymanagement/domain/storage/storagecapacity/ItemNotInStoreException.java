package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

public class ItemNotInStoreException extends Exception {
    public ItemNotInStoreException(String message) {
        super(message);
    }
}
