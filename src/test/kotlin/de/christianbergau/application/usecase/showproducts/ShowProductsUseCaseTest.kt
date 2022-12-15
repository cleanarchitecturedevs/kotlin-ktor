package de.christianbergau.application.usecase.showproducts

import de.christianbergau.application.entity.Product
import de.christianbergau.application.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ShowProductsUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldPresentDto() = runTest {
        // arrange
        val presenter = mockk<ShowProductsPresenter>()
        coEvery { presenter.presentProducts(any()) } returns Unit

        val products = listOf(Product(id = 1, ean = "123456789012"))
        val repository = mockk<ProductRepository>()
        coEvery { repository.all() } returns products

        val useCase = ShowProductsUseCase(presenter = presenter, repository = repository)

        // act
        useCase.execute()

        // assert
        coVerify { presenter.presentProducts(listOf(ProductDto(id = 1, ean = "123456789012"))) }
    }
}
