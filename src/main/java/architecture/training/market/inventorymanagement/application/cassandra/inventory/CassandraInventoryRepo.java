package architecture.training.market.inventorymanagement.application.cassandra.inventory;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface CassandraInventoryRepo extends CassandraRepository<InventoryEventEntity, EventPrimaryKey> {
    List<InventoryEventEntity> findAllById_Id(String id);
    void deleteAllById_Id(String id);
}
