package com.sendilkumarn.sample.kproduct.web.rest

import com.sendilkumarn.sample.kproduct.KproductApp
import com.sendilkumarn.sample.kproduct.domain.OrderItem
import com.sendilkumarn.sample.kproduct.domain.Product
import com.sendilkumarn.sample.kproduct.domain.ProductOrder
import com.sendilkumarn.sample.kproduct.domain.enumeration.OrderItemStatus
import com.sendilkumarn.sample.kproduct.repository.OrderItemRepository
import com.sendilkumarn.sample.kproduct.service.OrderItemService
import com.sendilkumarn.sample.kproduct.web.rest.errors.ExceptionTranslator
import java.math.BigDecimal
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [OrderItemResource] REST controller.
 *
 * @see OrderItemResource
 */
@SpringBootTest(classes = [KproductApp::class])
@AutoConfigureMockMvc
@WithMockUser
class OrderItemResourceIT {

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var orderItemService: OrderItemService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restOrderItemMockMvc: MockMvc

    private lateinit var orderItem: OrderItem

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val orderItemResource = OrderItemResource(orderItemService)
         this.restOrderItemMockMvc = MockMvcBuilders.standaloneSetup(orderItemResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        orderItem = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createOrderItem() {
        val databaseSizeBeforeCreate = orderItemRepository.findAll().size

        // Create the OrderItem
        restOrderItemMockMvc.perform(
            post("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(orderItem))
        ).andExpect(status().isCreated)

        // Validate the OrderItem in the database
        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeCreate + 1)
        val testOrderItem = orderItemList[orderItemList.size - 1]
        assertThat(testOrderItem.quantity).isEqualTo(DEFAULT_QUANTITY)
        assertThat(testOrderItem.totalPrice).isEqualTo(DEFAULT_TOTAL_PRICE)
        assertThat(testOrderItem.status).isEqualTo(DEFAULT_STATUS)
    }

    @Test
    @Transactional
    fun createOrderItemWithExistingId() {
        val databaseSizeBeforeCreate = orderItemRepository.findAll().size

        // Create the OrderItem with an existing ID
        orderItem.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemMockMvc.perform(
            post("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(orderItem))
        ).andExpect(status().isBadRequest)

        // Validate the OrderItem in the database
        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkQuantityIsRequired() {
        val databaseSizeBeforeTest = orderItemRepository.findAll().size
        // set the field null
        orderItem.quantity = null

        // Create the OrderItem, which fails.

        restOrderItemMockMvc.perform(
            post("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(orderItem))
        ).andExpect(status().isBadRequest)

        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkTotalPriceIsRequired() {
        val databaseSizeBeforeTest = orderItemRepository.findAll().size
        // set the field null
        orderItem.totalPrice = null

        // Create the OrderItem, which fails.

        restOrderItemMockMvc.perform(
            post("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(orderItem))
        ).andExpect(status().isBadRequest)

        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStatusIsRequired() {
        val databaseSizeBeforeTest = orderItemRepository.findAll().size
        // set the field null
        orderItem.status = null

        // Create the OrderItem, which fails.

        restOrderItemMockMvc.perform(
            post("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(orderItem))
        ).andExpect(status().isBadRequest)

        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllOrderItems() {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem)

        // Get all the orderItemList
        restOrderItemMockMvc.perform(get("/api/order-items?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItem.id?.toInt())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE?.toInt())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString()))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getOrderItem() {
        // Initialize the database
        orderItemRepository.saveAndFlush(orderItem)

        val id = orderItem.id
        assertNotNull(id)

        // Get the orderItem
        restOrderItemMockMvc.perform(get("/api/order-items/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItem.id?.toInt()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.totalPrice").value(DEFAULT_TOTAL_PRICE?.toInt()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString())) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingOrderItem() {
        // Get the orderItem
        restOrderItemMockMvc.perform(get("/api/order-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateOrderItem() {
        // Initialize the database
        orderItemService.save(orderItem)

        val databaseSizeBeforeUpdate = orderItemRepository.findAll().size

        // Update the orderItem
        val id = orderItem.id
        assertNotNull(id)
        val updatedOrderItem = orderItemRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOrderItem are not directly saved in db
        em.detach(updatedOrderItem)
        updatedOrderItem.quantity = UPDATED_QUANTITY
        updatedOrderItem.totalPrice = UPDATED_TOTAL_PRICE
        updatedOrderItem.status = UPDATED_STATUS

        restOrderItemMockMvc.perform(
            put("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedOrderItem))
        ).andExpect(status().isOk)

        // Validate the OrderItem in the database
        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate)
        val testOrderItem = orderItemList[orderItemList.size - 1]
        assertThat(testOrderItem.quantity).isEqualTo(UPDATED_QUANTITY)
        assertThat(testOrderItem.totalPrice).isEqualTo(UPDATED_TOTAL_PRICE)
        assertThat(testOrderItem.status).isEqualTo(UPDATED_STATUS)
    }

    @Test
    @Transactional
    fun updateNonExistingOrderItem() {
        val databaseSizeBeforeUpdate = orderItemRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemMockMvc.perform(
            put("/api/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(orderItem))
        ).andExpect(status().isBadRequest)

        // Validate the OrderItem in the database
        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteOrderItem() {
        // Initialize the database
        orderItemService.save(orderItem)

        val databaseSizeBeforeDelete = orderItemRepository.findAll().size

        // Delete the orderItem
        restOrderItemMockMvc.perform(
            delete("/api/order-items/{id}", orderItem.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val orderItemList = orderItemRepository.findAll()
        assertThat(orderItemList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_QUANTITY: Int = 0
        private const val UPDATED_QUANTITY: Int = 1

        private val DEFAULT_TOTAL_PRICE: BigDecimal = BigDecimal(0)
        private val UPDATED_TOTAL_PRICE: BigDecimal = BigDecimal(1)

        private val DEFAULT_STATUS: OrderItemStatus = OrderItemStatus.AVAILABLE
        private val UPDATED_STATUS: OrderItemStatus = OrderItemStatus.OUT_OF_STOCK

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): OrderItem {
            val orderItem = OrderItem(
                quantity = DEFAULT_QUANTITY,
                totalPrice = DEFAULT_TOTAL_PRICE,
                status = DEFAULT_STATUS
            )

            // Add required entity
            val product: Product
            if (em.findAll(Product::class).isEmpty()) {
                product = ProductResourceIT.createEntity(em)
                em.persist(product)
                em.flush()
            } else {
                product = em.findAll(Product::class).get(0)
            }
            orderItem.product = product
            // Add required entity
            val productOrder: ProductOrder
            if (em.findAll(ProductOrder::class).isEmpty()) {
                productOrder = ProductOrderResourceIT.createEntity(em)
                em.persist(productOrder)
                em.flush()
            } else {
                productOrder = em.findAll(ProductOrder::class).get(0)
            }
            orderItem.order = productOrder
            return orderItem
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): OrderItem {
            val orderItem = OrderItem(
                quantity = UPDATED_QUANTITY,
                totalPrice = UPDATED_TOTAL_PRICE,
                status = UPDATED_STATUS
            )

            // Add required entity
            val product: Product
            if (em.findAll(Product::class).isEmpty()) {
                product = ProductResourceIT.createUpdatedEntity(em)
                em.persist(product)
                em.flush()
            } else {
                product = em.findAll(Product::class).get(0)
            }
            orderItem.product = product
            // Add required entity
            val productOrder: ProductOrder
            if (em.findAll(ProductOrder::class).isEmpty()) {
                productOrder = ProductOrderResourceIT.createUpdatedEntity(em)
                em.persist(productOrder)
                em.flush()
            } else {
                productOrder = em.findAll(ProductOrder::class).get(0)
            }
            orderItem.order = productOrder
            return orderItem
        }
    }
}
