package de.christianbergau.application.validation

import io.konform.validation.ValidationErrors

class ValidationError(val errors: ValidationErrors) : Error()
