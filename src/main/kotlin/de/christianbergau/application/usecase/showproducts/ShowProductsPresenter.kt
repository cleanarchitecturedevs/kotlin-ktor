package de.christianbergau.application.usecase.showproducts

interface ShowProductsPresenter {
    suspend fun presentProducts(products: List<ProductDto>)
    suspend fun presentInternalServerError(message: String)
}
