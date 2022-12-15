package de.christianbergau.productexposed.repository

import de.christianbergau.application.entity.Product
import de.christianbergau.application.repository.ProductRepository
import de.christianbergau.productexposed.helpers.DatabaseFactory.dbQuery
import de.christianbergau.productexposed.model.Products
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class ProductRepositoryImpl : ProductRepository {
    override suspend fun save(product: Product): Product? = dbQuery {
        val stmt = Products.insert {
            it[ean] = product.ean
        }
        stmt.resultedValues?.singleOrNull()?.let(::resultRowToProduct)
    }

    override suspend fun all(): List<Product> = dbQuery {
        Products
            .selectAll()
            .map(::resultRowToProduct)
    }

    private fun resultRowToProduct(row: ResultRow) = Product(
        id = row[Products.id],
        ean = row[Products.ean]
    )
}
