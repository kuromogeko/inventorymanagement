/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package architecture.training.market.inventorymanagement.domain;

/**
 *
 * @author Admin
 */
public interface DomainEventPublisher {
    void publishEvent(DomainEvent event);
}
