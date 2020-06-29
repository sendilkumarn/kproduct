package com.sendilkumarn.sample.kproduct.domain

import com.sendilkumarn.sample.kproduct.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductCategoryTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductCategory::class)
        val productCategory1 = ProductCategory()
        productCategory1.id = 1L
        val productCategory2 = ProductCategory()
        productCategory2.id = productCategory1.id
        assertThat(productCategory1).isEqualTo(productCategory2)
        productCategory2.id = 2L
        assertThat(productCategory1).isNotEqualTo(productCategory2)
        productCategory1.id = null
        assertThat(productCategory1).isNotEqualTo(productCategory2)
    }
}
