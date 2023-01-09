package de.christianbergau.restapi

import de.christianbergau.productexposed.helpers.DatabaseFactory
import de.christianbergau.restapi.plugins.configureRouting
import de.christianbergau.restapi.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureRouting()
}
