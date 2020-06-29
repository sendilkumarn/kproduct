package com.sendilkumarn.sample.kproduct.web.rest

import com.sendilkumarn.sample.kproduct.domain.ProductOrder
import com.sendilkumarn.sample.kproduct.service.ProductOrderService
import com.sendilkumarn.sample.kproduct.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "kproductProductOrder"
/**
 * REST controller for managing [com.sendilkumarn.sample.kproduct.domain.ProductOrder].
 */
@RestController
@RequestMapping("/api")
class ProductOrderResource(
    private val productOrderService: ProductOrderService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /product-orders` : Create a new productOrder.
     *
     * @param productOrder the productOrder to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productOrder, or with status `400 (Bad Request)` if the productOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-orders")
    fun createProductOrder(@Valid @RequestBody productOrder: ProductOrder): ResponseEntity<ProductOrder> {
        log.debug("REST request to save ProductOrder : {}", productOrder)
        if (productOrder.id != null) {
            throw BadRequestAlertException(
                "A new productOrder cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = productOrderService.save(productOrder)
        return ResponseEntity.created(URI("/api/product-orders/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /product-orders` : Updates an existing productOrder.
     *
     * @param productOrder the productOrder to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productOrder,
     * or with status `400 (Bad Request)` if the productOrder is not valid,
     * or with status `500 (Internal Server Error)` if the productOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-orders")
    fun updateProductOrder(@Valid @RequestBody productOrder: ProductOrder): ResponseEntity<ProductOrder> {
        log.debug("REST request to update ProductOrder : {}", productOrder)
        if (productOrder.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = productOrderService.save(productOrder)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     productOrder.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /product-orders` : get all the productOrders.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productOrders in body.
     */
    @GetMapping("/product-orders")
    fun getAllProductOrders(pageable: Pageable): ResponseEntity<List<ProductOrder>> {
        log.debug("REST request to get a page of ProductOrders")
        val page = productOrderService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /product-orders/:id` : get the "id" productOrder.
     *
     * @param id the id of the productOrder to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productOrder, or with status `404 (Not Found)`.
     */
    @GetMapping("/product-orders/{id}")
    fun getProductOrder(@PathVariable id: Long): ResponseEntity<ProductOrder> {
        log.debug("REST request to get ProductOrder : {}", id)
        val productOrder = productOrderService.findOne(id)
        return ResponseUtil.wrapOrNotFound(productOrder)
    }
    /**
     *  `DELETE  /product-orders/:id` : delete the "id" productOrder.
     *
     * @param id the id of the productOrder to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/product-orders/{id}")
    fun deleteProductOrder(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ProductOrder : {}", id)

        productOrderService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
