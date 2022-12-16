package de.christianbergau.application.usecase.addproducts

import de.christianbergau.application.repository.ProductRepository
import de.christianbergau.application.usecase.addproduct.AddProductPresenter
import de.christianbergau.application.usecase.addproduct.AddProductRequest
import de.christianbergau.application.usecase.addproduct.AddProductUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AddProductsUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldPresentValidationErrors() = runTest {
        // arrange
        val presenter = mockk<AddProductPresenter>()
        coEvery { presenter.presentValidationError(any()) } returns Unit
        val repository = mockk<ProductRepository>()
        val useCase = AddProductUseCase(presenter = presenter, repository = repository)

        // act
        useCase.execute(AddProductRequest(ean = "123"))

        // assert
        coVerify {
            presenter.presentValidationError(
                listOf(
                    mapOf(".ean" to "must have at least 13 characters")
                )
            )
        }
    }
}
