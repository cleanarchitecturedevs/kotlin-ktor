package de.christianbergau.productexposed.model

import org.jetbrains.exposed.sql.Table

object Products : Table() {
    val id = integer("id").autoIncrement()
    val ean = varchar("ean", 13)

    override val primaryKey = PrimaryKey(id)
}
