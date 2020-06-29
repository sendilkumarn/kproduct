package com.sendilkumarn.sample.kproduct.service
import com.sendilkumarn.sample.kproduct.domain.ProductOrder
import com.sendilkumarn.sample.kproduct.repository.ProductOrderRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [ProductOrder].
 */
@Service
@Transactional
class ProductOrderService(
    private val productOrderRepository: ProductOrderRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a productOrder.
     *
     * @param productOrder the entity to save.
     * @return the persisted entity.
     */
    fun save(productOrder: ProductOrder): ProductOrder {
        log.debug("Request to save ProductOrder : {}", productOrder)
        return productOrderRepository.save(productOrder)
    }

    /**
     * Get all the productOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ProductOrder> {
        log.debug("Request to get all ProductOrders")
        return productOrderRepository.findAll(pageable)
    }

    /**
     * Get one productOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ProductOrder> {
        log.debug("Request to get ProductOrder : {}", id)
        return productOrderRepository.findById(id)
    }

    /**
     * Delete the productOrder by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete ProductOrder : {}", id)

        productOrderRepository.deleteById(id)
    }
}
