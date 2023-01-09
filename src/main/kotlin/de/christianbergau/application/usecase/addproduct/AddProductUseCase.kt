package de.christianbergau.application.usecase.addproduct

import de.christianbergau.application.entity.Product
import de.christianbergau.application.repository.ProductRepository
import de.christianbergau.application.validation.*
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern

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
        request to
                ::validate then
                ::save then
                ::presentSuccess otherwise
                ::presentError
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
            return Failure(ValidationError(validationResult.errors))
        }

        return Success(Product(id = 0, ean = request.ean))
    }

    private suspend fun save(product: Product): Result<Product> {
        try {
            val newProduct = repository.save(product)
                ?: return Failure(Error("New Product was not saved"))

            return Success(newProduct)
        } catch (e: Throwable) {
            return Failure(Error("Internal Error during saving Product"))
        }
    }

    private suspend fun presentSuccess(product: Product): Result<String> {
        presenter.product(
            AddProductDto(
                id = product.id,
                ean = product.ean
            )
        )

        return Success("ok")
    }
}

