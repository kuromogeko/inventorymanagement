package architecture.training.market.inventorymanagement.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StorageTypeTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Test
    public void testConstructorWithNullName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new StorageType(null, "Description", domainEventPublisher);
        });
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new StorageType("", "Description", domainEventPublisher);
        });
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithNullDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new StorageType("Name", null, domainEventPublisher);
        });
        assertEquals("Description cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new StorageType("Name", "", domainEventPublisher);
        });
        assertEquals("Description cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorHappyPath() {
        StorageType storageType = new StorageType("Name", "Description", domainEventPublisher);
        assertEquals(true, storageType.getUuid() != null && !storageType.getUuid().toString().isEmpty());
        ArgumentCaptor<StorageTypeCreatedEvent> captor = ArgumentCaptor.forClass(StorageTypeCreatedEvent.class);
        verify(domainEventPublisher, times(1)).publishEvent(captor.capture());
        var cap = captor.getValue();
        assertEquals("Name", cap.name());
        assertEquals("Description", cap.description());
        assertNotNull(cap.uuid());
    }

}
