package de.christianbergau.application.usecase.addproduct

import de.christianbergau.application.entity.Product
import de.christianbergau.application.repository.ProductRepository
import de.christianbergau.application.validation.ValidationError
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import kotlinx.coroutines.runBlocking

infix fun <T, U> T.to(f: (T) -> Result<U>) = Result.success(this).map(f)

class AddProductUseCase constructor(
    private val presenter: AddProductPresenter,
    private val repository: ProductRepository
) {
    private val validateProduct = Validation {
        AddProductRequest::ean required {
            minLength(13)
            maxLength(13)
            pattern("(\\d)+")
        }
    }

    suspend fun execute(request: AddProductRequest) {
        validate(request)
            .map { product -> save(product) }
            .map { result -> presentSuccess(result.getOrThrow()) }
            .onFailure { error ->  presentError(error) }
    }

    private suspend fun presentError(err: Throwable) {
        if (err is ValidationError) {
            return presenter.validationErrors(err.errors.map { ve ->
                mapOf(ve.dataPath to ve.message)
            })
        }

        return presenter.internalError("${err.message}")
    }

    private fun validate(request: AddProductRequest): Result<Product> {
        val validationResult = validateProduct.validate(request)

        if (validationResult.errors.isNotEmpty()) {
            return Result.failure(ValidationError(validationResult.errors))
        }

        return Result.success(Product(id = 0, ean = request.ean))
    }

    private suspend fun save(product: Product): Result<Product> {
        try {
            val newProduct = repository.save(product)

            if (newProduct == null) {
                return Result.failure(Error("New Product was not saved"));
            }

            return Result.success(newProduct)
        } catch (e: Throwable) {
            return Result.failure(Error("Internal Error during saving Product"))
        }
    }

    private suspend fun presentSuccess(product: Product) {
        presenter.product(
            AddProductDto(
                id = product.id,
                ean = product.ean
            )
        )
    }
}

