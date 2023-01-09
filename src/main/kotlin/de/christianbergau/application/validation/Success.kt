package de.christianbergau.application.validation

data class Success<T>(val value: T): Result<T>()
