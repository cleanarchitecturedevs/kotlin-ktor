package de.christianbergau.application.entity

class Product constructor(
    val id: Int,
    val ean: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (id != other.id) return false
        if (ean != other.ean) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + ean.hashCode()
        return result
    }
}
