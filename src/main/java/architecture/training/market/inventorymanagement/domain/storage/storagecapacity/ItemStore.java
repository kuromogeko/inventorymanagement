package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.UUID;

public record ItemStore(UUID itemId, ItemAmount amount)  {

}
