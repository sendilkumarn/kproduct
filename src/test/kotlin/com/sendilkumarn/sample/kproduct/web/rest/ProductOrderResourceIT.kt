package com.sendilkumarn.sample.kproduct.web.rest

import com.sendilkumarn.sample.kproduct.KproductApp
import com.sendilkumarn.sample.kproduct.domain.ProductOrder
import com.sendilkumarn.sample.kproduct.domain.enumeration.OrderStatus
import com.sendilkumarn.sample.kproduct.repository.ProductOrderRepository
import com.sendilkumarn.sample.kproduct.service.ProductOrderService
import com.sendilkumarn.sample.kproduct.web.rest.errors.ExceptionTranslator
import java.time.Instant
import java.time.temporal.ChronoUnit
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
 * Integration tests for the [ProductOrderResource] REST controller.
 *
 * @see ProductOrderResource
 */
@SpringBootTest(classes = [KproductApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ProductOrderResourceIT {

    @Autowired
    private lateinit var productOrderRepository: ProductOrderRepository

    @Autowired
    private lateinit var productOrderService: ProductOrderService

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

    private lateinit var restProductOrderMockMvc: MockMvc

    private lateinit var productOrder: ProductOrder

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val productOrderResource = ProductOrderResource(productOrderService)
         this.restProductOrderMockMvc = MockMvcBuilders.standaloneSetup(productOrderResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        productOrder = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductOrder() {
        val databaseSizeBeforeCreate = productOrderRepository.findAll().size

        // Create the ProductOrder
        restProductOrderMockMvc.perform(
            post("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productOrder))
        ).andExpect(status().isCreated)

        // Validate the ProductOrder in the database
        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeCreate + 1)
        val testProductOrder = productOrderList[productOrderList.size - 1]
        assertThat(testProductOrder.placedDate).isEqualTo(DEFAULT_PLACED_DATE)
        assertThat(testProductOrder.status).isEqualTo(DEFAULT_STATUS)
        assertThat(testProductOrder.code).isEqualTo(DEFAULT_CODE)
        assertThat(testProductOrder.invoiceId).isEqualTo(DEFAULT_INVOICE_ID)
        assertThat(testProductOrder.customer).isEqualTo(DEFAULT_CUSTOMER)
    }

    @Test
    @Transactional
    fun createProductOrderWithExistingId() {
        val databaseSizeBeforeCreate = productOrderRepository.findAll().size

        // Create the ProductOrder with an existing ID
        productOrder.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductOrderMockMvc.perform(
            post("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productOrder))
        ).andExpect(status().isBadRequest)

        // Validate the ProductOrder in the database
        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkPlacedDateIsRequired() {
        val databaseSizeBeforeTest = productOrderRepository.findAll().size
        // set the field null
        productOrder.placedDate = null

        // Create the ProductOrder, which fails.

        restProductOrderMockMvc.perform(
            post("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productOrder))
        ).andExpect(status().isBadRequest)

        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStatusIsRequired() {
        val databaseSizeBeforeTest = productOrderRepository.findAll().size
        // set the field null
        productOrder.status = null

        // Create the ProductOrder, which fails.

        restProductOrderMockMvc.perform(
            post("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productOrder))
        ).andExpect(status().isBadRequest)

        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkCodeIsRequired() {
        val databaseSizeBeforeTest = productOrderRepository.findAll().size
        // set the field null
        productOrder.code = null

        // Create the ProductOrder, which fails.

        restProductOrderMockMvc.perform(
            post("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productOrder))
        ).andExpect(status().isBadRequest)

        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkCustomerIsRequired() {
        val databaseSizeBeforeTest = productOrderRepository.findAll().size
        // set the field null
        productOrder.customer = null

        // Create the ProductOrder, which fails.

        restProductOrderMockMvc.perform(
            post("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productOrder))
        ).andExpect(status().isBadRequest)

        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProductOrders() {
        // Initialize the database
        productOrderRepository.saveAndFlush(productOrder)

        // Get all the productOrderList
        restProductOrderMockMvc.perform(get("/api/product-orders?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productOrder.id?.toInt())))
            .andExpect(jsonPath("$.[*].placedDate").value(hasItem(DEFAULT_PLACED_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].invoiceId").value(hasItem(DEFAULT_INVOICE_ID?.toInt())))
            .andExpect(jsonPath("$.[*].customer").value(hasItem(DEFAULT_CUSTOMER))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProductOrder() {
        // Initialize the database
        productOrderRepository.saveAndFlush(productOrder)

        val id = productOrder.id
        assertNotNull(id)

        // Get the productOrder
        restProductOrderMockMvc.perform(get("/api/product-orders/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productOrder.id?.toInt()))
            .andExpect(jsonPath("$.placedDate").value(DEFAULT_PLACED_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.invoiceId").value(DEFAULT_INVOICE_ID?.toInt()))
            .andExpect(jsonPath("$.customer").value(DEFAULT_CUSTOMER)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProductOrder() {
        // Get the productOrder
        restProductOrderMockMvc.perform(get("/api/product-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateProductOrder() {
        // Initialize the database
        productOrderService.save(productOrder)

        val databaseSizeBeforeUpdate = productOrderRepository.findAll().size

        // Update the productOrder
        val id = productOrder.id
        assertNotNull(id)
        val updatedProductOrder = productOrderRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedProductOrder are not directly saved in db
        em.detach(updatedProductOrder)
        updatedProductOrder.placedDate = UPDATED_PLACED_DATE
        updatedProductOrder.status = UPDATED_STATUS
        updatedProductOrder.code = UPDATED_CODE
        updatedProductOrder.invoiceId = UPDATED_INVOICE_ID
        updatedProductOrder.customer = UPDATED_CUSTOMER

        restProductOrderMockMvc.perform(
            put("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedProductOrder))
        ).andExpect(status().isOk)

        // Validate the ProductOrder in the database
        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeUpdate)
        val testProductOrder = productOrderList[productOrderList.size - 1]
        assertThat(testProductOrder.placedDate).isEqualTo(UPDATED_PLACED_DATE)
        assertThat(testProductOrder.status).isEqualTo(UPDATED_STATUS)
        assertThat(testProductOrder.code).isEqualTo(UPDATED_CODE)
        assertThat(testProductOrder.invoiceId).isEqualTo(UPDATED_INVOICE_ID)
        assertThat(testProductOrder.customer).isEqualTo(UPDATED_CUSTOMER)
    }

    @Test
    @Transactional
    fun updateNonExistingProductOrder() {
        val databaseSizeBeforeUpdate = productOrderRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductOrderMockMvc.perform(
            put("/api/product-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productOrder))
        ).andExpect(status().isBadRequest)

        // Validate the ProductOrder in the database
        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProductOrder() {
        // Initialize the database
        productOrderService.save(productOrder)

        val databaseSizeBeforeDelete = productOrderRepository.findAll().size

        // Delete the productOrder
        restProductOrderMockMvc.perform(
            delete("/api/product-orders/{id}", productOrder.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productOrderList = productOrderRepository.findAll()
        assertThat(productOrderList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_PLACED_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_PLACED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_STATUS: OrderStatus = OrderStatus.COMPLETED
        private val UPDATED_STATUS: OrderStatus = OrderStatus.PENDING

        private const val DEFAULT_CODE = "AAAAAAAAAA"
        private const val UPDATED_CODE = "BBBBBBBBBB"

        private const val DEFAULT_INVOICE_ID: Long = 1L
        private const val UPDATED_INVOICE_ID: Long = 2L

        private const val DEFAULT_CUSTOMER = "AAAAAAAAAA"
        private const val UPDATED_CUSTOMER = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ProductOrder {
            val productOrder = ProductOrder(
                placedDate = DEFAULT_PLACED_DATE,
                status = DEFAULT_STATUS,
                code = DEFAULT_CODE,
                invoiceId = DEFAULT_INVOICE_ID,
                customer = DEFAULT_CUSTOMER
            )

            return productOrder
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ProductOrder {
            val productOrder = ProductOrder(
                placedDate = UPDATED_PLACED_DATE,
                status = UPDATED_STATUS,
                code = UPDATED_CODE,
                invoiceId = UPDATED_INVOICE_ID,
                customer = UPDATED_CUSTOMER
            )

            return productOrder
        }
    }
}
