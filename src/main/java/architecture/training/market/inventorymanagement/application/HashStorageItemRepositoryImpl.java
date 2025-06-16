package architecture.training.market.inventorymanagement.application;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import architecture.training.market.inventorymanagement.domain.storage.items.StorageItem;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItemCreatedEvent;
import architecture.training.market.inventorymanagement.domain.storage.items.StorageItemRepository;

public class HashStorageItemRepositoryImpl implements StorageItemRepository{

        private final HashMap<UUID, StorageItemCreatedEvent> repo;

        public HashStorageItemRepositoryImpl() {
                this.repo = new HashMap<>();
        }
    @Override
    public Optional<StorageItem> findById(UUID id) {
        return Optional.ofNullable(this.repo.get(id)).map(StorageItem::applyCreate);
    }
    

}
