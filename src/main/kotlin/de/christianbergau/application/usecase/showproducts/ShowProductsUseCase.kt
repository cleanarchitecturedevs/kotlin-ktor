package de.christianbergau.application.usecase.showproducts

import de.christianbergau.application.repository.ProductRepository

class ShowProductsUseCase constructor(
    private val presenter: ShowProductsPresenter,
    private val repository: ProductRepository
) {
    suspend fun execute() {
        val products = repository.all()
        presenter.presentProducts(products.map { product -> ProductDto(product.id, product.ean) })
    }
}
