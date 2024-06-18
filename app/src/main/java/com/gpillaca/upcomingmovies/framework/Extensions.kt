package com.gpillaca.upcomingmovies.framework

import com.gpillaca.upcomingmovies.domain.common.Either
import com.gpillaca.upcomingmovies.domain.common.Error
import com.gpillaca.upcomingmovies.domain.common.left
import com.gpillaca.upcomingmovies.domain.common.right
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toError() = when(this) {
    is IOException -> Error.Connectivity
    is HttpException -> Error.Server(code())
    else -> Error.Unknown(message ?: "")
}

@JvmName("tryCatch")
inline fun <T> catch(function: () -> T): Either<Error, T> = try {
    function().right()
} catch (exception: Exception) {
    exception.toError().left()
}
