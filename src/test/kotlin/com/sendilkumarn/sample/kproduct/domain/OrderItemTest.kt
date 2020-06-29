package com.sendilkumarn.sample.kproduct.domain

import com.sendilkumarn.sample.kproduct.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderItemTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(OrderItem::class)
        val orderItem1 = OrderItem()
        orderItem1.id = 1L
        val orderItem2 = OrderItem()
        orderItem2.id = orderItem1.id
        assertThat(orderItem1).isEqualTo(orderItem2)
        orderItem2.id = 2L
        assertThat(orderItem1).isNotEqualTo(orderItem2)
        orderItem1.id = null
        assertThat(orderItem1).isNotEqualTo(orderItem2)
    }
}
