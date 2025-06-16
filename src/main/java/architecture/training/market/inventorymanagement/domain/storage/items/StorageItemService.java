package architecture.training.market.inventorymanagement.domain.storage.items;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import architecture.training.market.inventorymanagement.domain.storage.StorageType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StorageItemService {
    private final StorageItemRepository repository;
    private final DomainEventPublisher publisher;

    public StorageItemService(StorageItemRepository repository, DomainEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public StorageItem createStorageItem(UUID itemId, List<StorageType> allowedTypes){
        return StorageItem.create(itemId, allowedTypes, publisher);
    }

    @EventListener
    public void handleStorageItemCreation(StorageItemCreatedEvent event){
        repository.save(event);
    }
}
