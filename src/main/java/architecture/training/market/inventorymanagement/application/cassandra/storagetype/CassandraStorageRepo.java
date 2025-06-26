package architecture.training.market.inventorymanagement.application.cassandra.storagetype;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface CassandraStorageRepo extends CassandraRepository<StorageTypeEventEntity, EventPrimaryKey> {
    List<StorageTypeEventEntity> findAllById_Id(String id);
    void deleteAllById_Id(String id);
}
