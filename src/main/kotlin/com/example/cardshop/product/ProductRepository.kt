package com.example.cardshop.product

import com.example.cardshop.product.dto.ProductWithStockCountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
//import java.awt.print.Pageable
import java.util.*

interface ProductRepository : JpaRepository<Product, Long> {

    fun findByIdAndIsActiveTrue(@Param("id") id: Long): Optional<Product>


    @Query(
        """
            SELECT new com.example.cardshop.product.dto.ProductWithStockCountDto(
                p.id, p.title, p.price, p.imageUrl, p.description, p.pricingType, p.prepStatus, COUNT(s.id)
            )
            FROM Product p
            LEFT JOIN p.stockItems s ON s.sold = false AND s.order IS NULL
            WHERE p.id = :id AND p.isActive = true
            GROUP BY p.id, p.title, p.price, p.imageUrl, p.description, p.pricingType, p.prepStatus
        """
    )
    fun findByIdAndIsActiveTrueWithAvailableStock(@Param("id") id: Long): Optional<ProductWithStockCountDto>

    @Query(
        """
            SELECT new com.example.cardshop.product.dto.ProductWithStockCountDto(p.id, p.title, p.price, p.imageUrl, p.description, p.pricingType, p.prepStatus, COUNT(s.id)) FROM Product p
            LEFT JOIN p.stockItems s ON s.sold = false AND s.order IS NULL
            WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))
            AND (:isActive IS NULL OR p.isActive = :isActive)
            GROUP BY p.id, p.title, p.price, p.imageUrl, p.description, p.pricingType, p.prepStatus
        """
    )
    fun searchActiveProductsWithAvailableStock(
        @Param("title") title: String,
        @Param("isActive") isActive: Boolean?,
        pageable: Pageable
    ): Page<ProductWithStockCountDto>
}