package com.sendilkumarn.sample.kproduct.service
import com.sendilkumarn.sample.kproduct.domain.OrderItem
import com.sendilkumarn.sample.kproduct.repository.OrderItemRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [OrderItem].
 */
@Service
@Transactional
class OrderItemService(
    private val orderItemRepository: OrderItemRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a orderItem.
     *
     * @param orderItem the entity to save.
     * @return the persisted entity.
     */
    fun save(orderItem: OrderItem): OrderItem {
        log.debug("Request to save OrderItem : {}", orderItem)
        return orderItemRepository.save(orderItem)
    }

    /**
     * Get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<OrderItem> {
        log.debug("Request to get all OrderItems")
        return orderItemRepository.findAll(pageable)
    }

    /**
     * Get one orderItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<OrderItem> {
        log.debug("Request to get OrderItem : {}", id)
        return orderItemRepository.findById(id)
    }

    /**
     * Delete the orderItem by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete OrderItem : {}", id)

        orderItemRepository.deleteById(id)
    }
}
