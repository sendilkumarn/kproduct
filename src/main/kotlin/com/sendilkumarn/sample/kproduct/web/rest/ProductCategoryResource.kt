package com.sendilkumarn.sample.kproduct.web.rest

import com.sendilkumarn.sample.kproduct.domain.ProductCategory
import com.sendilkumarn.sample.kproduct.service.ProductCategoryService
import com.sendilkumarn.sample.kproduct.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "kproductProductCategory"
/**
 * REST controller for managing [com.sendilkumarn.sample.kproduct.domain.ProductCategory].
 */
@RestController
@RequestMapping("/api")
class ProductCategoryResource(
    private val productCategoryService: ProductCategoryService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /product-categories` : Create a new productCategory.
     *
     * @param productCategory the productCategory to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productCategory, or with status `400 (Bad Request)` if the productCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-categories")
    fun createProductCategory(@Valid @RequestBody productCategory: ProductCategory): ResponseEntity<ProductCategory> {
        log.debug("REST request to save ProductCategory : {}", productCategory)
        if (productCategory.id != null) {
            throw BadRequestAlertException(
                "A new productCategory cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = productCategoryService.save(productCategory)
        return ResponseEntity.created(URI("/api/product-categories/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /product-categories` : Updates an existing productCategory.
     *
     * @param productCategory the productCategory to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productCategory,
     * or with status `400 (Bad Request)` if the productCategory is not valid,
     * or with status `500 (Internal Server Error)` if the productCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-categories")
    fun updateProductCategory(@Valid @RequestBody productCategory: ProductCategory): ResponseEntity<ProductCategory> {
        log.debug("REST request to update ProductCategory : {}", productCategory)
        if (productCategory.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = productCategoryService.save(productCategory)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     productCategory.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /product-categories` : get all the productCategories.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productCategories in body.
     */
    @GetMapping("/product-categories")
    fun getAllProductCategories(): MutableList<ProductCategory> {
        log.debug("REST request to get all ProductCategories")

        return productCategoryService.findAll()
            }

    /**
     * `GET  /product-categories/:id` : get the "id" productCategory.
     *
     * @param id the id of the productCategory to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productCategory, or with status `404 (Not Found)`.
     */
    @GetMapping("/product-categories/{id}")
    fun getProductCategory(@PathVariable id: Long): ResponseEntity<ProductCategory> {
        log.debug("REST request to get ProductCategory : {}", id)
        val productCategory = productCategoryService.findOne(id)
        return ResponseUtil.wrapOrNotFound(productCategory)
    }
    /**
     *  `DELETE  /product-categories/:id` : delete the "id" productCategory.
     *
     * @param id the id of the productCategory to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/product-categories/{id}")
    fun deleteProductCategory(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ProductCategory : {}", id)

        productCategoryService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
