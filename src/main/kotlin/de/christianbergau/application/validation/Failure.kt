package de.christianbergau.application.validation

data class Failure<T>(val throwable: Throwable): Result<T>()
