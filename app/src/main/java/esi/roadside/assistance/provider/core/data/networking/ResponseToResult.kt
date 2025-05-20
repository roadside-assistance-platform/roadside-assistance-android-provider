package esi.roadside.assistance.provider.core.data.networking

import esi.roadside.assistance.provider.core.domain.util.Result
import esi.roadside.assistance.provider.core.domain.util.Result.Error
import esi.roadside.assistance.provider.core.domain.util.Result.Success
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> responseToResult(
    callType: CallType? = null,
    response: HttpResponse,
): Result<T, DomainError> {
    val error400 = when (callType) {
        CallType.SIGNUP -> DomainError.USER_ALREADY_EXISTS
        CallType.LOGIN -> DomainError.USER_NOT_FOUND
        CallType.RESET_PASSWORD -> DomainError.USER_NOT_FOUND
        CallType.UPDATE -> DomainError.USER_NOT_FOUND
        CallType.GOOGLE -> DomainError.UNKNOWN
        CallType.HOME -> DomainError.UNKNOWN
        CallType.SEND_EMAIL -> DomainError.EMAIL_ERROR
        CallType.VERIFY_EMAIL -> DomainError.INCORRECT_VERIFICATION_CODE
        null -> DomainError.BAD_REQUEST
    }
    val error401 = when (callType) {
        CallType.SIGNUP -> DomainError.USER_ALREADY_EXISTS
        CallType.LOGIN -> DomainError.INCORRECT_CREDENTIALS
        CallType.RESET_PASSWORD -> DomainError.INTERNAL_ERROR
        CallType.UPDATE -> DomainError.INTERNAL_ERROR
        CallType.GOOGLE -> DomainError.UNKNOWN
        CallType.HOME -> DomainError.UNKNOWN
        else -> DomainError.UNKNOWN
    }
    val error403 = DomainError.USER_DELETED
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Success(response.body<T>())
            } catch (_: NoTransformationFoundException) {
                Error(DomainError.SERIALIZATION_ERROR)
            }
        }
        400 -> Error(error400)
        401 -> Error(error401)
        403 -> Error(error403)
        500 -> Error(DomainError.SERVER_ERROR)
        502 -> Error(DomainError.SERVER_UNAVAILABLE)
        else -> Error(DomainError.UNKNOWN)
    }
}