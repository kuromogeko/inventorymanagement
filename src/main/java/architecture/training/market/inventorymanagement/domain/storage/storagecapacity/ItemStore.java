package architecture.training.market.inventorymanagement.domain.storage.storagecapacity;

import java.util.UUID;

public record ItemStore(UUID itemId, ItemAmount amount)  {


@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemStore that)) return false;

    return itemId.equals(that.itemId);
}

@Override
public int hashCode() {
    return itemId.hashCode();
}

}
