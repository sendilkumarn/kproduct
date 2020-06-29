package com.sendilkumarn.sample.kproduct.repository

import com.sendilkumarn.sample.kproduct.domain.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [OrderItem] entity.
 */
@Suppress("unused")
@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long>
