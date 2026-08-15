package com.plantappkmp.platform.network

import io.ktor.client.engine.HttpClientEngine

/** The platform's HTTP transport: OkHttp on Android, `NSURLSession` on iOS. */
expect fun platformHttpEngine(): HttpClientEngine
