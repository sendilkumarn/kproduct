package com.sendilkumarn.sample.kproduct.repository

import com.sendilkumarn.sample.kproduct.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Product] entity.
 */
@Suppress("unused")
@Repository
interface ProductRepository : JpaRepository<Product, Long>
