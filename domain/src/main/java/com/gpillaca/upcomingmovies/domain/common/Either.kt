package com.gpillaca.upcomingmovies.domain.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class Either<out ErrorType, out ResultType> {
    data class Left<ErrorType>(val error: ErrorType) : Either<ErrorType, Nothing>()
    data class Right<ResultType>(val value: ResultType) : Either<Nothing, ResultType>()

    inline fun <T> fold(ifLeft: (ErrorType) -> T, ifRight: (ResultType) -> T): T =
        when (this) {
            is Left -> ifLeft(error)
            is Right -> ifRight(value)
        }

    @JvmName("mapRight")
    inline fun <NewResultType> map(
        functionRight: (ResultType) -> NewResultType
    ): Either<ErrorType, NewResultType> {
        return when (this) {
            is Left -> this
            is Right -> Right(functionRight(this.value))
        }
    }

    @JvmName("mapLeft")
    inline fun <NewErrorType> map(
        functionLeft: (ErrorType) -> NewErrorType
    ): Either<NewErrorType, ResultType> {
        return when (this) {
            is Left -> Left(functionLeft(this.error))
            is Right -> this
        }
    }

    suspend fun <NewErrorType, NewResultType> concat(
        function: suspend (ResultType) -> Either<NewErrorType, NewResultType>,
        errorTransformation: (error: ErrorType) -> Either<NewErrorType, Nothing>
    ): Either<NewErrorType, NewResultType> {
        return when (this) {
            is Right -> function(this.value)
            is Left -> errorTransformation(error)
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun isLeft(): Boolean {
        contract { returns(true) implies (this@Either is Left<ErrorType>) }
        return this@Either is Left<ErrorType>
    }

    @OptIn(ExperimentalContracts::class)
    fun isRight(): Boolean {
        contract { returns(true) implies (this@Either is Right<ResultType>) }
        return this@Either is Right<ResultType>
    }

    inline fun onLeft(
        function: (failure: ErrorType) -> Unit
    ): Either<ErrorType, ResultType> =
        also { if (it.isLeft()) function(it.error) }

    inline fun onRight(
        function: (success: ResultType) -> Unit
    ): Either<ErrorType, ResultType> =
        also { if (it.isRight()) function(it.value) }
}

fun <ErrorType> ErrorType.left(): Either<ErrorType, Nothing> = Either.Left(this)

fun <ResultType> ResultType.right(): Either<Nothing, ResultType> = Either.Right(this)
