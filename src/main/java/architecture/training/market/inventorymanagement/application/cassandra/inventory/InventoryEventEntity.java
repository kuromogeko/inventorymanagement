package architecture.training.market.inventorymanagement.application.cassandra.inventory;

import architecture.training.market.inventorymanagement.application.cassandra.EventPrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("inventory")
public class InventoryEventEntity {
    @PrimaryKey private final EventPrimaryKey id;
    private final String event;
    private final String eventType;

    public InventoryEventEntity(EventPrimaryKey id, String event, String eventType) {
        this.id = id;
        this.event = event;
        this.eventType = eventType;
    }

    public EventPrimaryKey getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }

    public String getEventType() {
        return eventType;
    }
}
