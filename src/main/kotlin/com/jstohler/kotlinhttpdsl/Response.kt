package com.jstohler.kotlinhttpdsl

import java.net.http.HttpRequest

data class Response(
    val statusCode: Int,
    val response: String,
    val request: HttpRequest
)