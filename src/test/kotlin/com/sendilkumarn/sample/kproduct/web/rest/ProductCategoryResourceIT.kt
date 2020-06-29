package com.sendilkumarn.sample.kproduct.web.rest

import com.sendilkumarn.sample.kproduct.KproductApp
import com.sendilkumarn.sample.kproduct.domain.ProductCategory
import com.sendilkumarn.sample.kproduct.repository.ProductCategoryRepository
import com.sendilkumarn.sample.kproduct.service.ProductCategoryService
import com.sendilkumarn.sample.kproduct.web.rest.errors.ExceptionTranslator
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
 * Integration tests for the [ProductCategoryResource] REST controller.
 *
 * @see ProductCategoryResource
 */
@SpringBootTest(classes = [KproductApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ProductCategoryResourceIT {

    @Autowired
    private lateinit var productCategoryRepository: ProductCategoryRepository

    @Autowired
    private lateinit var productCategoryService: ProductCategoryService

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

    private lateinit var restProductCategoryMockMvc: MockMvc

    private lateinit var productCategory: ProductCategory

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val productCategoryResource = ProductCategoryResource(productCategoryService)
         this.restProductCategoryMockMvc = MockMvcBuilders.standaloneSetup(productCategoryResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        productCategory = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductCategory() {
        val databaseSizeBeforeCreate = productCategoryRepository.findAll().size

        // Create the ProductCategory
        restProductCategoryMockMvc.perform(
            post("/api/product-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategory))
        ).andExpect(status().isCreated)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeCreate + 1)
        val testProductCategory = productCategoryList[productCategoryList.size - 1]
        assertThat(testProductCategory.name).isEqualTo(DEFAULT_NAME)
        assertThat(testProductCategory.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    fun createProductCategoryWithExistingId() {
        val databaseSizeBeforeCreate = productCategoryRepository.findAll().size

        // Create the ProductCategory with an existing ID
        productCategory.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductCategoryMockMvc.perform(
            post("/api/product-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategory))
        ).andExpect(status().isBadRequest)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = productCategoryRepository.findAll().size
        // set the field null
        productCategory.name = null

        // Create the ProductCategory, which fails.

        restProductCategoryMockMvc.perform(
            post("/api/product-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategory))
        ).andExpect(status().isBadRequest)

        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProductCategories() {
        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        // Get all the productCategoryList
        restProductCategoryMockMvc.perform(get("/api/product-categories?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productCategory.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProductCategory() {
        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        val id = productCategory.id
        assertNotNull(id)

        // Get the productCategory
        restProductCategoryMockMvc.perform(get("/api/product-categories/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productCategory.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProductCategory() {
        // Get the productCategory
        restProductCategoryMockMvc.perform(get("/api/product-categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateProductCategory() {
        // Initialize the database
        productCategoryService.save(productCategory)

        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size

        // Update the productCategory
        val id = productCategory.id
        assertNotNull(id)
        val updatedProductCategory = productCategoryRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedProductCategory are not directly saved in db
        em.detach(updatedProductCategory)
        updatedProductCategory.name = UPDATED_NAME
        updatedProductCategory.description = UPDATED_DESCRIPTION

        restProductCategoryMockMvc.perform(
            put("/api/product-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedProductCategory))
        ).andExpect(status().isOk)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
        val testProductCategory = productCategoryList[productCategoryList.size - 1]
        assertThat(testProductCategory.name).isEqualTo(UPDATED_NAME)
        assertThat(testProductCategory.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun updateNonExistingProductCategory() {
        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductCategoryMockMvc.perform(
            put("/api/product-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategory))
        ).andExpect(status().isBadRequest)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProductCategory() {
        // Initialize the database
        productCategoryService.save(productCategory)

        val databaseSizeBeforeDelete = productCategoryRepository.findAll().size

        // Delete the productCategory
        restProductCategoryMockMvc.perform(
            delete("/api/product-categories/{id}", productCategory.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ProductCategory {
            val productCategory = ProductCategory(
                name = DEFAULT_NAME,
                description = DEFAULT_DESCRIPTION
            )

            return productCategory
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ProductCategory {
            val productCategory = ProductCategory(
                name = UPDATED_NAME,
                description = UPDATED_DESCRIPTION
            )

            return productCategory
        }
    }
}
