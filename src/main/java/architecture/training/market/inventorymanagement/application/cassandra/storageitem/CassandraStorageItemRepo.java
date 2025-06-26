package architecture.training.market.inventorymanagement.application.cassandra.storageitem;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface CassandraStorageItemRepo extends CassandraRepository<StorageItemEventEntity, EventPrimaryKey> {
    List<StorageItemEventEntity> findAllById_Id(String id);
    void deleteAllById_Id(String id);
}
