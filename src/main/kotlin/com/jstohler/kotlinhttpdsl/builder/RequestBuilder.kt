package com.jstohler.kotlinhttpdsl.builder

interface RequestBuilder {
    fun header(header: () -> String)

    fun headers(headerList: () -> Collection<String>)

    fun get(address: String, payload: (() -> Any)? = null)

    fun put(address: String, payload: (() -> Any)? = null)

    fun post(address: String, payload: (() -> Any)? = null)

    fun head(address: String, payload: (() -> Any)? = null)

    fun patch(address: String, payload: (() -> Any)? = null)

    fun trace(address: String, payload: (() -> Any)? = null)

    fun delete(address: String, payload: (() -> Any)? = null)

    fun options(address: String, payload: (() -> Any)? = null)

    // Connect currently isn't supported with HttpRequest
    // fun connect(address: String, payload: (() -> Any)? = null)
}