package com.jstohler.kotlinhttpdsl

data class Response(
    val statusCode: Int,
    val response: String,
    val request: Request
)