package com.example.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String? = { null },
    private val institutionIdProvider: () -> String = { "00000000-0000-0000-0000-000000000001" },
    private val roleProvider: () -> String = { "ADMIN" }
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val token = tokenProvider()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        } else {
            // Development fallback headers
            requestBuilder.header("X-Dev-Role", roleProvider())
        }

        requestBuilder.header("X-Institution-ID", institutionIdProvider())
        requestBuilder.header("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}

