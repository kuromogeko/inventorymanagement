package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

public class UnloadGreaterThanStoredAmoundException extends Exception {

    public UnloadGreaterThanStoredAmoundException(String messsage) {
        super(messsage);
    }

}
