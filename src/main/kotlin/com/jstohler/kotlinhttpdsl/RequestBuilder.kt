package com.jstohler.kotlinhttpdsl

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class RequestBuilder {
    private var verb: HttpVerb? = null
    private var target: String? = null
    private var payload: String? = null

    private val headers = mutableSetOf<String>()

    fun header(header: () -> String) = headers.add(header.invoke())

    fun headers(headerList: () -> Collection<String>) = headers.addAll(headerList.invoke())

    fun get(address: () -> String) = fill(HttpVerb.GET, address.invoke())

    fun post(address: () -> String, body: () -> String?) = fill(HttpVerb.POST, address.invoke(), body.invoke())

    fun put(address: () -> String, body: () -> String?) = fill(HttpVerb.PUT, address.invoke(), body.invoke())

    fun delete(address: () -> String, body: () -> String?) = fill(HttpVerb.DELETE, address.invoke(), body.invoke())

    fun head(address: () -> String, body: () -> String?) = fill(HttpVerb.HEAD, address.invoke(), body.invoke())

    fun options(address: () -> String, body: () -> String?) = fill(HttpVerb.OPTIONS, address.invoke(), body.invoke())

    fun patch(address: () -> String, body: () -> String?) = fill(HttpVerb.PATCH, address.invoke(), body.invoke())

    fun trace(address: () -> String, body: () -> String?) = fill(HttpVerb.TRACE, address.invoke(), body.invoke())

    fun connect(address: () -> String, body: () -> String?) = fill(HttpVerb.CONNECT, address.invoke(), body.invoke())

    fun body(body: () -> String) = this.apply { payload = body.invoke() }

    fun build(): Request {
        requireNotNull(verb)
        requireNotNull(target)

        return Request(target!!, headers, payload).apply { httpVerb = verb!! }
    }

    private fun fill(inputVerb: HttpVerb, address: String, body: String? = null) {
        verb = inputVerb
        target = address
        payload = body
    }

    companion object {
        fun build(block: RequestBuilder.() -> Unit): Request = RequestBuilder().apply(block).build()
        fun request(block: RequestBuilder.() -> Unit): Response = RequestBuilder().apply(block).request()

        private val client = HttpClient.newBuilder().build()

        @JvmStatic
        fun RequestBuilder.request(): Response {
            val requestObj = this.build()

            val request = HttpRequest.newBuilder().setData(requestObj).build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            return Response(response.statusCode(), response.body(), requestObj)
        }

        private fun HttpRequest.Builder.setData(request: Request): HttpRequest.Builder {
            val uri = URI.create(request.target)
            val bodyPublisher = HttpRequest.BodyPublishers.ofString(request.body)

            return this.uri(uri).method(request.httpVerb.name, bodyPublisher).headers(*(request.headers.toTypedArray()))
        }

        @Suppress("unused")
        private fun HttpRequest.Builder.setMethod(verb: HttpVerb, body: String?): HttpRequest.Builder {
            return when (verb) {
                HttpVerb.GET -> this.method("GET", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.POST -> this.method("POST", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.PUT -> this.method("PUT", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.DELETE -> this.method("DELETE", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.HEAD -> this.method("HEAD", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.OPTIONS -> this.method("OPTIONS", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.PATCH -> this.method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.TRACE -> this.method("TRACE", HttpRequest.BodyPublishers.ofString(body))
                HttpVerb.CONNECT -> this.method("CONNECT", HttpRequest.BodyPublishers.ofString(body))
            }
        }
    }
}
