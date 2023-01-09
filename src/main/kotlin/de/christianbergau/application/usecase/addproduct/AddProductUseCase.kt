package de.christianbergau.application.usecase.addproduct

import de.christianbergau.application.entity.Product
import de.christianbergau.application.repository.ProductRepository
import de.christianbergau.application.validation.ValidationError
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern

sealed class MyResult<T>
data class MySuccess<T>(val value: T): MyResult<T>()
data class MyFailure<T>(val throwable: Throwable): MyResult<T>()

// Composition: apply a function f to Success results
suspend infix fun <T,U> MyResult<T>.then(f: suspend (T) -> MyResult<U>) =
    when (this) {
        is MySuccess -> f(this.value)
        is MyFailure -> MyFailure(this.throwable)
    }

// Pipe input: the beginning of a railway
suspend infix fun <T,U> T.to(f: suspend (T) -> MyResult<U>) = MySuccess(this) then f

// Handle error output: the end of a railway
suspend infix fun <T> MyResult<T>.otherwise(f: suspend (Throwable) -> Unit) =
    if (this is MyFailure) f(this.throwable) else Unit

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

    private suspend fun validate(request: AddProductRequest): MyResult<Product> {
        val validationResult = validateProduct.validate(request)

        if (validationResult.errors.isNotEmpty()) {
            return MyFailure(ValidationError(validationResult.errors))
        }

        return MySuccess(Product(id = 0, ean = request.ean))
    }

    private suspend fun save(product: Product): MyResult<Product> {
        try {
            val newProduct = repository.save(product)
                ?: return MyFailure(Error("New Product was not saved"))

            return MySuccess(newProduct)
        } catch (e: Throwable) {
            return MyFailure(Error("Internal Error during saving Product"))
        }
    }

    private suspend fun presentSuccess(product: Product): MyResult<String> {
        presenter.product(
            AddProductDto(
                id = product.id,
                ean = product.ean
            )
        )

        return MySuccess("ok")
    }
}

