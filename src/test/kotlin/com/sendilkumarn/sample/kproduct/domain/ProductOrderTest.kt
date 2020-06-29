package com.sendilkumarn.sample.kproduct.domain

import com.sendilkumarn.sample.kproduct.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductOrderTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductOrder::class)
        val productOrder1 = ProductOrder()
        productOrder1.id = 1L
        val productOrder2 = ProductOrder()
        productOrder2.id = productOrder1.id
        assertThat(productOrder1).isEqualTo(productOrder2)
        productOrder2.id = 2L
        assertThat(productOrder1).isNotEqualTo(productOrder2)
        productOrder1.id = null
        assertThat(productOrder1).isNotEqualTo(productOrder2)
    }
}
