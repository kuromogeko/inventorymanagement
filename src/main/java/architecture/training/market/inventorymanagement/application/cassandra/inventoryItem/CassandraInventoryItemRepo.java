package architecture.training.market.inventorymanagement.application.cassandra.inventoryItem;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface CassandraInventoryItemRepo extends CassandraRepository<InventoryItemEventEntity, EventPrimaryKey> {
    List<InventoryItemEventEntity> findAllById_Id(String id);
    void deleteAllById_Id(String id);
}
