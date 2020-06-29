package com.sendilkumarn.sample.kproduct.repository

import com.sendilkumarn.sample.kproduct.domain.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ProductCategory] entity.
 */
@Suppress("unused")
@Repository
interface ProductCategoryRepository : JpaRepository<ProductCategory, Long>
