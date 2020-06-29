package com.sendilkumarn.sample.kproduct.service
import com.sendilkumarn.sample.kproduct.domain.ProductCategory
import com.sendilkumarn.sample.kproduct.repository.ProductCategoryRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [ProductCategory].
 */
@Service
@Transactional
class ProductCategoryService(
    private val productCategoryRepository: ProductCategoryRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a productCategory.
     *
     * @param productCategory the entity to save.
     * @return the persisted entity.
     */
    fun save(productCategory: ProductCategory): ProductCategory {
        log.debug("Request to save ProductCategory : {}", productCategory)
        return productCategoryRepository.save(productCategory)
    }

    /**
     * Get all the productCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(): MutableList<ProductCategory> {
        log.debug("Request to get all ProductCategories")
        return productCategoryRepository.findAll()
    }

    /**
     * Get one productCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ProductCategory> {
        log.debug("Request to get ProductCategory : {}", id)
        return productCategoryRepository.findById(id)
    }

    /**
     * Delete the productCategory by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete ProductCategory : {}", id)

        productCategoryRepository.deleteById(id)
    }
}
