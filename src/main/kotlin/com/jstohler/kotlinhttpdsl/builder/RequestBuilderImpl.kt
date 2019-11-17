package com.jstohler.kotlinhttpdsl.builder

import com.jstohler.kotlinhttpdsl.HttpVerb
import com.jstohler.kotlinhttpdsl.Request
import com.jstohler.kotlinhttpdsl.Response
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class RequestBuilderImpl : RequestBuilder {
    private var verb: HttpVerb? = null
    private var target: String? = null
    private var body: String? = null
    private val headers = mutableSetOf<String>()

    override fun header(header: () -> String) {
        this.headers.add(header.invoke())
    }

    override fun headers(headerList: () -> Collection<String>) {
        headers.addAll(headerList.invoke())
    }

    override fun get(address: String, payload: (() -> Any)?) = fill(HttpVerb.GET, address, payload)

    override fun put(address: String, payload: (() -> Any)?) = fill(HttpVerb.PUT, address, payload)

    override fun post(address: String, payload: (() -> Any)?) = fill(HttpVerb.POST, address, payload)

    override fun head(address: String, payload: (() -> Any)?) = fill(HttpVerb.HEAD, address, payload)

    override fun patch(address: String, payload: (() -> Any)?) = fill(HttpVerb.PATCH, address, payload)

    override fun trace(address: String, payload: (() -> Any)?) = fill(HttpVerb.TRACE, address, payload)

    override fun delete(address: String, payload: (() -> Any)?) = fill(HttpVerb.DELETE, address, payload)

    override fun options(address: String, payload: (() -> Any)?) = fill(HttpVerb.OPTIONS, address, payload)

    // Connect currently isn't supported with HttpRequest
    // override fun connect(address: String, payload: (() -> Any)?) = fill(HttpVerb.CONNECT, address, payload)

    private fun fill(inputVerb: HttpVerb, address: String, payload: (() -> Any)?) {
        verb = inputVerb
        target = address
        body = payload?.invoke() as String?
    }

    private fun buildRequest(): Request {
        requireNotNull(verb)
        require(!target.isNullOrBlank())

        return Request(target!!, headers, body).apply { httpVerb = verb!! }
    }

    private fun buildAndSendRequest(): Response {
        val request = this.buildRequest()

        val uri = URI.create(request.target)

        val bodyPublisher = if (request.body == null) {
            HttpRequest.BodyPublishers.noBody()
        } else {
            HttpRequest.BodyPublishers.ofString(request.body)
        }

        val httpRequestBuilder = HttpRequest.newBuilder()
            .method(request.httpVerb.name, bodyPublisher)
            .uri(uri)

        request.headers.forEach {
            val (headerKey, headerValue) = extractHeaderValue(it)

            httpRequestBuilder.header(headerKey, headerValue)
        }

        val httpRequest = httpRequestBuilder.build()

        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())

        return Response(response.statusCode(), response.body(), httpRequest)
    }

    private fun extractHeaderValue(header: String): Pair<String, String> {
        val headerSplitList =  header.split(":")

        return headerSplitList.first().trim() to headerSplitList.last().trim()
    }

    companion object {
        private val client = HttpClient.newBuilder().build()

        fun build(block: RequestBuilderImpl.() -> Unit) = RequestBuilderImpl().apply(block).buildRequest()
        fun request(block: RequestBuilderImpl.() -> Unit) = RequestBuilderImpl().apply(block).buildAndSendRequest()
    }
}
