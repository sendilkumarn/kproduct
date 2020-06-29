package com.sendilkumarn.sample.kproduct.repository

import com.sendilkumarn.sample.kproduct.domain.ProductOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ProductOrder] entity.
 */
@Suppress("unused")
@Repository
interface ProductOrderRepository : JpaRepository<ProductOrder, Long>
