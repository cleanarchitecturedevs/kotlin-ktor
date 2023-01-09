package de.christianbergau.application.validation

sealed class Result<T>

// Composition: apply a function f to Success results
suspend infix fun <T,U> Result<T>.then(f: suspend (T) -> Result<U>) =
    when (this) {
        is Success -> f(this.value)
        is Failure -> Failure(this.throwable)
    }

// Pipe input: the beginning of a railway
suspend infix fun <T,U> T.to(f: suspend (T) -> Result<U>) = Success(this) then f

// Handle error output: the end of a railway
suspend infix fun <T> Result<T>.otherwise(f: suspend (Throwable) -> Unit) =
    if (this is Failure) f(this.throwable) else Unit
