package de.christianbergau.restapi.plugins

import de.christianbergau.application.usecase.addproduct.AddProductDto
import de.christianbergau.application.usecase.addproduct.AddProductPresenter
import de.christianbergau.application.usecase.addproduct.AddProductRequest
import de.christianbergau.application.usecase.addproduct.AddProductUseCase
import de.christianbergau.application.usecase.showproducts.ProductDto
import de.christianbergau.application.usecase.showproducts.ShowProductsPresenter
import de.christianbergau.application.usecase.showproducts.ShowProductsUseCase
import de.christianbergau.productexposed.repository.ProductRepositoryImpl
import de.christianbergau.restapi.v1.ProductApiModel
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.productsRouting() {
    route("/products") {
        get {
            ShowProductsUseCase(
                presenter = ShowProductsApiPresenter(context as RoutingApplicationCall),
                repository = ProductRepositoryImpl()
            ).execute()
        }

        post {
            val call = context as RoutingApplicationCall
            val request = call.receive<AddProduct>()

            AddProductUseCase(
                presenter = AddProductApiPresenter(context as RoutingApplicationCall),
                repository = ProductRepositoryImpl()
            ).execute(AddProductRequest(ean = request.ean))
        }
    }
}

@Serializable
data class AddProduct constructor(val ean: String)

class ShowProductsApiPresenter constructor(private val context: RoutingApplicationCall) : ShowProductsPresenter {
    override suspend fun presentProducts(products: List<ProductDto>) {
        val viewModel = products.map { productDto -> ProductApiModel(productDto.id, productDto.ean) }
        context.respond(viewModel)
    }

    override suspend fun presentInternalServerError(message: String) {
        context.respond(HttpStatusCode.InternalServerError, message)
    }
}

class AddProductApiPresenter constructor(private val context: RoutingApplicationCall) : AddProductPresenter {
    override suspend fun presentProduct(product: AddProductDto) {
        val viewModel = ProductApiModel(product.id, product.ean)
        context.respond(viewModel)
    }

    override suspend fun presentValidationError(errors: List<Map<String, String>>) {
        context.respond(HttpStatusCode.BadRequest, errors)
    }

    override suspend fun presentInternalServerError(message: String) {
        context.respond(HttpStatusCode.InternalServerError, message)
    }
}
