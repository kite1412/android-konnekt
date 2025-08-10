package nrr.konnekt.core.domain.util

sealed interface Result<out T, out E: Error> {
    data class Success<out T>(val data: T) : Result<T, Nothing>
    data class Error<out E: nrr.konnekt.core.domain.util.Error>(
        val error: E
    ) : Result<Nothing, E>
}

fun <T> Success(data: T) = Result.Success(data)

fun <E: Error> Error(error: E) = Result.Error(error)