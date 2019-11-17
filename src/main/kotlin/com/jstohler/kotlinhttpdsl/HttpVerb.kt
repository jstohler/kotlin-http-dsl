package com.jstohler.kotlinhttpdsl

/**
 * CONNECT currently is not supported in Java 11 HttpRequest class.
 */
internal enum class HttpVerb {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, TRACE //, CONNECT
}