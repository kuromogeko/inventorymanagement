package architecture.training.market.inventorymanagement.domain.sales;

import architecture.training.market.inventorymanagement.domain.storage.items.ItemService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SalesService {
    private final ItemService itemService;

    public SalesService(ItemService itemService) {
        this.itemService = itemService;
    }

    @EventListener
    public void handleItemSold(ItemSoldEvent event) {
        itemService.removeItem(event.itemId(), event.amount());
    }
}
