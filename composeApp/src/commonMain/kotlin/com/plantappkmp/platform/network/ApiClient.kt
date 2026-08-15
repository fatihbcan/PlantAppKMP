package com.plantappkmp.platform.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Endpoints and timeouts for the case API.
 */
object ApiConfig {
    const val BASE_URL = "https://dummy-api-jtg6bessta-ey.a.run.app/"

    const val CONNECT_TIMEOUT_MILLIS = 15_000L
    const val READ_TIMEOUT_MILLIS = 20_000L
    const val WRITE_TIMEOUT_MILLIS = 15_000L
}

fun appJson(): Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

/**
 * The shared HTTP client. [engine] is supplied per platform — OkHttp on
 * Android, Darwin on iOS — which is the only part of this stack that cannot be
 * written once.
 *
 * Note what is *not* installed: `ContentNegotiation`. Both endpoints answer
 * with `content-type: text/plain` despite returning JSON, and that plugin
 * dispatches its deserializer on exactly that header, so it would refuse every
 * response. Retrofit picks a converter by declared return type and never
 * noticed; the Flutter build of this app needs a whole interceptor to work
 * around the same server behaviour. [ApiClient] reads the body as text and
 * decodes it explicitly, which does not consult the header at all.
 */
fun appHttpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) {
    // Failures are raised from the status code in `ApiClient` instead, so that
    // classification lives in one place with the rest of the translation.
    expectSuccess = false

    install(HttpTimeout) {
        connectTimeoutMillis = ApiConfig.CONNECT_TIMEOUT_MILLIS
        requestTimeoutMillis = ApiConfig.READ_TIMEOUT_MILLIS
        socketTimeoutMillis = ApiConfig.WRITE_TIMEOUT_MILLIS
    }

    defaultRequest {
        url(ApiConfig.BASE_URL)
    }
}

/**
 * Status-code validation, JSON decoding and error translation, in one place,
 * so no data source repeats them and no repository has to know what an HTTP
 * code is.
 *
 * This is the multiplatform stand-in for the Android build's OkHttp
 * `ErrorInterceptor` plus its Retrofit converter, and it produces exactly the
 * same four [AppException] cases.
 */
class ApiClient(
    private val client: HttpClient,
    private val json: Json,
) {

    suspend fun <T> get(path: String, deserializer: DeserializationStrategy<T>): T = apiCall {
        val response = client.get(path)
        if (!response.status.isSuccess()) throw AppException.Server(response.status.value)
        json.decodeFromString(deserializer, response.bodyAsText())
    }
}

suspend inline fun <reified T> ApiClient.get(path: String): T = get(path, serializer())

/**
 * Runs [block] and lets nothing but an [AppException] out of it.
 *
 * `IllegalStateException` and friends are deliberately not caught: a bug in
 * our own code should crash loudly rather than reach the user as a network
 * failure.
 */
// `AppException.Server` carries the status code rather than the cause, exactly
// as it does in the Android build: the code is what a repository branches on,
// and the Ktor exception underneath it says nothing a log line would want.
@Suppress("SwallowedException")
suspend fun <T> apiCall(block: suspend () -> T): T = try {
    block()
} catch (cause: AppException) {
    throw cause
} catch (cause: HttpRequestTimeoutException) {
    throw AppException.Network(cause)
} catch (cause: ResponseException) {
    throw AppException.Server(cause.response.status.value)
} catch (cause: SerializationException) {
    throw AppException.Parse(cause)
} catch (cause: IOException) {
    // Ktor wraps DNS failures, refused connections and dropped sockets in this
    // on every engine, so it is the catch-all for "there was no usable link".
    throw AppException.Network(cause)
}
