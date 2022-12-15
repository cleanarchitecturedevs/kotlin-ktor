package de.christianbergau.application.usecase.addproduct

interface AddProductPresenter {
    suspend fun presentProduct(product: AddProductDto)
    suspend fun presentValidationError(errors: List<Map<String, String>>)
    suspend fun presentInternalServerError(message: String)
}
