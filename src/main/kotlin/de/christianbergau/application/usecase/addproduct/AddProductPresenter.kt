package de.christianbergau.application.usecase.addproduct

interface AddProductPresenter {
    suspend fun product(product: AddProductDto)
    suspend fun validationErrors(errors: List<Map<String, String>>)
    suspend fun internalError(message: String)
}
