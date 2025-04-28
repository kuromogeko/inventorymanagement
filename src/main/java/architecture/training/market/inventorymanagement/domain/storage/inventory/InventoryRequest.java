package architecture.training.market.inventorymanagement.domain.storage.inventory;

import java.util.List;
import java.util.UUID;

public record InventoryRequest(List<InventoryCount> itemCounts, UUID employeeId)  {

}
