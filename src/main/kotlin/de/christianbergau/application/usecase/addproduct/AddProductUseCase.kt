package de.christianbergau.application.usecase.addproduct

import de.christianbergau.application.entity.Product
import de.christianbergau.application.repository.ProductRepository
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
        val validationResult = validateProduct.validate(request)

        if (validationResult.errors.isNotEmpty()) {
            val errors = validationResult.errors.map { ve ->
                mapOf(ve.dataPath to ve.message)
            }

            return presenter.validationErrors(errors)
        }

        val newProduct: Product?

        try {
            newProduct = repository.save(Product(id = 0, ean = request.ean))
        } catch (e: Throwable) {
            return presenter.internalError("Internal Error during saving Product")
        }

        if (newProduct == null) {
            return presenter.internalError("unknown error");
        }

        presenter.product(
            AddProductDto(
                id = newProduct.id,
                ean = newProduct.ean
            )
        )
    }
}

