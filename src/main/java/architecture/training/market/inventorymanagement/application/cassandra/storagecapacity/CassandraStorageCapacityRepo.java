package architecture.training.market.inventorymanagement.application.cassandra.storagecapacity;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface CassandraStorageCapacityRepo extends CassandraRepository<StorageCapacityEventEntity, EventPrimaryKey> {
    List<StorageCapacityEventEntity> findAllById_Id(String id);
    void deleteAllById_Id(String id);
}
