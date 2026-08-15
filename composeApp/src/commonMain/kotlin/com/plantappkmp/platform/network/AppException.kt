package com.plantappkmp.platform.network

/**
 * The only exception type allowed to leave this package.
 *
 * The Android build extends `IOException` so that OkHttp's interceptor — which
 * may only throw one — can raise these directly. There is no interceptor here
 * and no `IOException` in common Kotlin, so it extends [Exception]: the
 * translation happens in [apiCall] instead, and the contract above the data
 * layer is unchanged.
 *
 * Nothing above `data` ever sees one of these. Repositories catch them and
 * return a sealed result case instead.
 */
sealed class AppException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {

    /** No usable connection: DNS failure, timeout, socket reset, airplane mode. */
    class Network(cause: Throwable? = null) : AppException("Network unavailable", cause)

    /** The server answered, but not with success. */
    class Server(val statusCode: Int) : AppException("Server returned $statusCode")

    /** The body arrived but did not match the contract. */
    class Parse(cause: Throwable? = null) : AppException("Malformed response body", cause)

    /** Anything the translation step could not classify. */
    class Unknown(cause: Throwable? = null) : AppException("Unknown network failure", cause)
}
