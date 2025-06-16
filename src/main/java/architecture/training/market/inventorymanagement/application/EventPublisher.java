package architecture.training.market.inventorymanagement.application;

import architecture.training.market.inventorymanagement.domain.DomainEvent;
import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    public EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publishEvent(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
