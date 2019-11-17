package com.jstohler.kotlinhttpdsl

data class Request(
    val target: String,
    val headers: Set<String>,
    val body: String?
) {
    internal lateinit var httpVerb: HttpVerb
}