package de.christianbergau.application.usecase.addproducts

import de.christianbergau.application.entity.Product
import de.christianbergau.application.repository.ProductRepository
import de.christianbergau.application.usecase.addproduct.AddProductDto
import de.christianbergau.application.usecase.addproduct.AddProductPresenter
import de.christianbergau.application.usecase.addproduct.AddProductRequest
import de.christianbergau.application.usecase.addproduct.AddProductUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductsUseCaseTest {

    @Test
    fun shouldPresentValidationErrors() = runTest {
        // arrange
        val present = mockk<AddProductPresenter>()
        coEvery { present.validationErrors(any()) } returns Unit
        val repository = mockk<ProductRepository>()
        val useCase = AddProductUseCase(presenter = present, repository = repository)

        // act
        useCase.execute(AddProductRequest(ean = "123"))

        // assert
        coVerify {
            present.validationErrors(
                listOf(
                    mapOf(".ean" to "must have at least 13 characters")
                )
            )
        }
    }

    @Test
    fun shouldPresentGeneralErrorWhenRepositoryThrowsException() = runTest {
        // arrange
        val present = mockk<AddProductPresenter>()
        coEvery { present.internalError("Internal Error during saving Product") } returns Unit
        val repository = mockk<ProductRepository>()
        coEvery { repository.save(any()) } throws IllegalStateException()
        val useCase = AddProductUseCase(presenter = present, repository = repository)

        // act
        useCase.execute(AddProductRequest(ean = "1234567890123"))

        // assert
        coVerify { present.internalError("Internal Error during saving Product") }
    }

    @Test
    fun shouldPresentGeneralErrorWhenRepositoryReturnedNoNewProduct() = runTest {
        // arrange
        val present = mockk<AddProductPresenter>()
        coEvery { present.internalError("unknown error") } returns Unit
        val repository = mockk<ProductRepository>()
        coEvery { repository.save(any()) } returns null
        val useCase = AddProductUseCase(presenter = present, repository = repository)

        // act
        useCase.execute(AddProductRequest(ean = "1234567890123"))

        // assert
        coVerify { present.internalError("unknown error") }
        coVerify { repository.save(Product(id = 0, ean = "1234567890123")) }
    }

    @Test
    fun shouldPresentNewProduct() = runTest {
        // arrange
        val present = mockk<AddProductPresenter>()
        coEvery { present.product(AddProductDto(id = 1, ean = "1234567890123")) } returns Unit
        val repository = mockk<ProductRepository>()
        coEvery { repository.save(any()) } returns Product(id = 1, ean = "1234567890123")
        val useCase = AddProductUseCase(presenter = present, repository = repository)

        // act
        useCase.execute(AddProductRequest(ean = "1234567890123"))

        // assert
        coVerify { present.product(AddProductDto(id = 1, ean = "1234567890123")) }
    }
}
