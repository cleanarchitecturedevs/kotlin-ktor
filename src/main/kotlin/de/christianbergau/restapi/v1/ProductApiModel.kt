package de.christianbergau.restapi.v1

import kotlinx.serialization.Serializable

@Serializable
data class ProductApiModel(val id: Int, val ean: String)
