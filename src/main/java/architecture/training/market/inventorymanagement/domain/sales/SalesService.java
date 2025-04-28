package architecture.training.market.inventorymanagement.domain.sales;

import architecture.training.market.inventorymanagement.domain.storage.items.ItemService;

public class SalesService {
    private final ItemService itemService;

    public SalesService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void handleItemSolt(ItemSoldEvent event) {
        itemService.removeItem(event.itemId(), event.amount());
    }
}
