package com.jstohler.kotlinhttpdsl.builder

import com.jstohler.kotlinhttpdsl.HttpVerb
import com.jstohler.kotlinhttpdsl.Request
import com.jstohler.kotlinhttpdsl.Response
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

class newBuilder {

    // TODO: I should just be able to grab all of these from the HttpVerb Classes
    private var verb: HttpVerb? = null
    private var target: String? = null
    private var body: String? = null
    private val headers = mutableSetOf<String>()

    @DslMarker
    annotation class VerbMarker

    @VerbMarker
    abstract class Verb {
        private val headers = mutableSetOf<String>()
        private var parts = listOf<Part>()

        fun header(header: () -> String) {
            this.headers.add(header())
        }

        fun headers(headerList: () -> Collection<String>) {
            this.headers.addAll(headerList())
        }

        fun multipart(boundary: String? = null, payload: MultiPart.() -> Unit) = MultiPart(boundary)
    }

    open class MultiPart(val boundary: String? = UUID.randomUUID().asBoundary()): Verb() {
        fun part(payload: Part.() -> Unit) = Part()
    }

    class Part(private var data: String = ""): MultiPart() {
        fun data(data: () -> String) {
            this.data = data()
        }
    }

    class Get : Verb()
    class Put : Verb()
    class Post : Verb()
    class Head : Verb()
    class Patch : Verb()
    class Trace : Verb()
    class Delete : Verb()
    class Options : Verb()

    fun get(address: String, payload: Get.() -> Unit) = Get()
    fun put(address: String, payload: Put.() -> Unit) = Put()
    fun post(address: String, payload: Post.() -> Unit) = Post()
    fun head(address: String, payload: Head.() -> Unit) = Head()
    fun patch(address: String, payload: Patch.() -> Unit) = Patch()
    fun trace(address: String, payload: Trace.() -> Unit) = Trace()
    fun delete(address: String, payload: Delete.() -> Unit) = Delete()
    fun options(address: String, payload: Options.() -> Unit) = Options()

    // TODO: Prevent verbs in verbs
    // TODO: Prevent parts in parts
    // TODO: Prevent headers in headers
    // TODO: Prevent data in data

    // TODO: Part REQUIRES one data
    // TODO: MultiPart REQUIRES one Part

    fun blah() {
        get("") {
            header { "" }

            multipart("blah") {
                part {
                    header { "" }
                    data { "" }
                }
                part { data { "" } }
                part { data { "" } }
                part { data { "" } }
            }
        }
    }

    // TODO: Figure this out:
    private fun buildRequest(): Request {
        requireNotNull(verb)
        require(!target.isNullOrBlank())

        return Request(target!!, headers, body).apply { httpVerb = verb!! }
    }

    // TODO: Figure this out:
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

    companion object {
        private val client = HttpClient.newBuilder().build()

        private fun UUID.asBoundary() = this.toString().toLowerCase().replace("-", "")

        private fun extractHeaderValue(header: String): Pair<String, String> {
            val headerSplitList =  header.split(":")

            return headerSplitList.first().trim() to headerSplitList.last().trim()
        }

        fun build(block: newBuilder.() -> Unit) = newBuilder().apply(block).buildRequest()
        fun request(block: newBuilder.() -> Unit) = newBuilder().apply(block).buildAndSendRequest()
    }
}