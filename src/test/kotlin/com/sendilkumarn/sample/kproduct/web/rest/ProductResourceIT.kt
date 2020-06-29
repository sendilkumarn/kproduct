package com.sendilkumarn.sample.kproduct.web.rest

import com.sendilkumarn.sample.kproduct.KproductApp
import com.sendilkumarn.sample.kproduct.domain.Product
import com.sendilkumarn.sample.kproduct.domain.enumeration.Size
import com.sendilkumarn.sample.kproduct.repository.ProductRepository
import com.sendilkumarn.sample.kproduct.service.ProductService
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
import org.springframework.util.Base64Utils
import org.springframework.validation.Validator

/**
 * Integration tests for the [ProductResource] REST controller.
 *
 * @see ProductResource
 */
@SpringBootTest(classes = [KproductApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var productService: ProductService

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

    private lateinit var restProductMockMvc: MockMvc

    private lateinit var product: Product

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val productResource = ProductResource(productService)
         this.restProductMockMvc = MockMvcBuilders.standaloneSetup(productResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        product = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProduct() {
        val databaseSizeBeforeCreate = productRepository.findAll().size

        // Create the Product
        restProductMockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(product))
        ).andExpect(status().isCreated)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1)
        val testProduct = productList[productList.size - 1]
        assertThat(testProduct.name).isEqualTo(DEFAULT_NAME)
        assertThat(testProduct.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testProduct.price).isEqualTo(DEFAULT_PRICE)
        assertThat(testProduct.size).isEqualTo(DEFAULT_SIZE)
        assertThat(testProduct.image).isEqualTo(DEFAULT_IMAGE)
        assertThat(testProduct.imageContentType).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun createProductWithExistingId() {
        val databaseSizeBeforeCreate = productRepository.findAll().size

        // Create the Product with an existing ID
        product.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = productRepository.findAll().size
        // set the field null
        product.name = null

        // Create the Product, which fails.

        restProductMockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest)

        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceIsRequired() {
        val databaseSizeBeforeTest = productRepository.findAll().size
        // set the field null
        product.price = null

        // Create the Product, which fails.

        restProductMockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest)

        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkSizeIsRequired() {
        val databaseSizeBeforeTest = productRepository.findAll().size
        // set the field null
        product.size = null

        // Create the Product, which fails.

        restProductMockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest)

        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProducts() {
        // Initialize the database
        productRepository.saveAndFlush(product)

        // Get all the productList
        restProductMockMvc.perform(get("/api/products?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE?.toInt())))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProduct() {
        // Initialize the database
        productRepository.saveAndFlush(product)

        val id = product.id
        assertNotNull(id)

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE?.toInt()))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProduct() {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateProduct() {
        // Initialize the database
        productService.save(product)

        val databaseSizeBeforeUpdate = productRepository.findAll().size

        // Update the product
        val id = product.id
        assertNotNull(id)
        val updatedProduct = productRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct)
        updatedProduct.name = UPDATED_NAME
        updatedProduct.description = UPDATED_DESCRIPTION
        updatedProduct.price = UPDATED_PRICE
        updatedProduct.size = UPDATED_SIZE
        updatedProduct.image = UPDATED_IMAGE
        updatedProduct.imageContentType = UPDATED_IMAGE_CONTENT_TYPE

        restProductMockMvc.perform(
            put("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedProduct))
        ).andExpect(status().isOk)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
        val testProduct = productList[productList.size - 1]
        assertThat(testProduct.name).isEqualTo(UPDATED_NAME)
        assertThat(testProduct.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testProduct.price).isEqualTo(UPDATED_PRICE)
        assertThat(testProduct.size).isEqualTo(UPDATED_SIZE)
        assertThat(testProduct.image).isEqualTo(UPDATED_IMAGE)
        assertThat(testProduct.imageContentType).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun updateNonExistingProduct() {
        val databaseSizeBeforeUpdate = productRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(
            put("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProduct() {
        // Initialize the database
        productService.save(product)

        val databaseSizeBeforeDelete = productRepository.findAll().size

        // Delete the product
        restProductMockMvc.perform(
            delete("/api/products/{id}", product.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private val DEFAULT_PRICE: BigDecimal = BigDecimal(0)
        private val UPDATED_PRICE: BigDecimal = BigDecimal(1)

        private val DEFAULT_SIZE: Size = Size.S
        private val UPDATED_SIZE: Size = Size.M

        private val DEFAULT_IMAGE: ByteArray = createByteArray(1, "0")
        private val UPDATED_IMAGE: ByteArray = createByteArray(1, "1")
        private const val DEFAULT_IMAGE_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_IMAGE_CONTENT_TYPE: String = "image/png"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Product {
            val product = Product(
                name = DEFAULT_NAME,
                description = DEFAULT_DESCRIPTION,
                price = DEFAULT_PRICE,
                size = DEFAULT_SIZE,
                image = DEFAULT_IMAGE,
                imageContentType = DEFAULT_IMAGE_CONTENT_TYPE
            )

            return product
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Product {
            val product = Product(
                name = UPDATED_NAME,
                description = UPDATED_DESCRIPTION,
                price = UPDATED_PRICE,
                size = UPDATED_SIZE,
                image = UPDATED_IMAGE,
                imageContentType = UPDATED_IMAGE_CONTENT_TYPE
            )

            return product
        }
    }
}
