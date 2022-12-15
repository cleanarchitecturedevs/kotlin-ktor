package de.christianbergau.application.repository

import de.christianbergau.application.entity.Product

interface ProductRepository {
    suspend fun save(product: Product): Product?
    suspend fun all(): List<Product>
}
