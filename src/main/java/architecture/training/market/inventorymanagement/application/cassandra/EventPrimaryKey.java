package architecture.training.market.inventorymanagement.application.cassandra;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.Instant;

@PrimaryKeyClass
public class EventPrimaryKey {

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.PARTITIONED)
    private String id;

    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private Instant timestamp;

    public EventPrimaryKey(String id, Instant timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }
}
