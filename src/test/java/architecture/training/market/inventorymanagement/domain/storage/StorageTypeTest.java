package architecture.training.market.inventorymanagement.domain.storage;

import architecture.training.market.inventorymanagement.domain.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageTypeTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Test
    public void testConstructorWithNullName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> StorageType.create(null, "Description", domainEventPublisher));
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> StorageType.create("", "Description", domainEventPublisher));
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithNullDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> StorageType.create("Name", null, domainEventPublisher));
        assertEquals("Description cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorWithEmptyDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> StorageType.create("Name", "", domainEventPublisher));
        assertEquals("Description cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testConstructorHappyPath() {
        StorageType storageType = StorageType.create("Name", "Description", domainEventPublisher);
        assertTrue(storageType.getUuid() != null && !storageType.getUuid().toString().isEmpty());
        ArgumentCaptor<StorageTypeCreatedEvent> captor = ArgumentCaptor.forClass(StorageTypeCreatedEvent.class);
        verify(domainEventPublisher, times(1)).publishEvent(captor.capture());
        var cap = captor.getValue();
        assertEquals("Name", cap.name());
        assertEquals("Description", cap.description());
        assertNotNull(cap.uuid());
    }

    @Test
    void updatesShouldBeApplied() {
        StorageType storageType = StorageType.create("Name", "Description", domainEventPublisher);
        storageType.update(new StorageTypeUpdateRequest("New Name", "New Desc"), domainEventPublisher);
        ArgumentCaptor<StorageTypeUpdatedEvent> updateCaptor = ArgumentCaptor.forClass(StorageTypeUpdatedEvent.class);
        verify(domainEventPublisher, times(1)).publishEvent(updateCaptor.capture());
        storageType.applyUpdate(updateCaptor.getValue());
        assertEquals("New Name", storageType.getName());
        assertEquals("New Desc", storageType.getDescription());
    }

    @Test
    void equalsShouldUseIdentity() {
        StorageType storageType = StorageType.create("Name", "Description", domainEventPublisher);
        StorageType storageTypeTwo = StorageType.create("Name", "Description", domainEventPublisher);
        assertNotEquals(storageType, storageTypeTwo);
    }
}
