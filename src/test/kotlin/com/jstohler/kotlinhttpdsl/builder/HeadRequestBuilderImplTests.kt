package com.jstohler.kotlinhttpdsl.builder

import com.jstohler.kotlinhttpdsl.HttpVerb
import com.jstohler.kotlinhttpdsl.Matchers
import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import org.junit.jupiter.api.*
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.verify.VerificationTimes
import java.net.http.HttpRequest

class HeadRequestBuilderImplTests : AbstractMockServer() {

    @Test
    fun `build HEAD with body Test`() {
        val request = RequestBuilderImpl.build {
            head("https://jstohler.com") {
                "{'some-json': 'body'}"
            }
            header { "Authorization: Bearer some_token" }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.HEAD))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, present(equalTo("{'some-json': 'body'}")))

        assertThat(request.headers, present(hasSize(equalTo(1))))
        assertThat(request.headers, Matchers.headerContains("Authorization: Bearer some_token"))
    }

    @Test
    fun `build HEAD with no body Test`() {
        val request = RequestBuilderImpl.build {
            head("https://jstohler.com")
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.HEAD))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, absent())

        assertThat(request.headers, present(isEmpty))
    }

    @Test
    fun `build HEAD with header and no body Test`() {
        val request = RequestBuilderImpl.build {
            head("https://jstohler.com")
            header { "Authorization: Bearer some_token" }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.HEAD))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, absent())
        assertThat(request.headers, present(hasSize(equalTo(1))))
        assertThat(request.headers, Matchers.headerContains("Authorization: Bearer some_token"))
    }

    @Test
    fun `build HEAD with multiple header blocks Test`() {
        val request = RequestBuilderImpl.build {
            head("https://jstohler.com") {
                "{'some-json': 'body'}"
            }
            header { "Authorization: Bearer some_token" }
            header { "Content-Type: application/text" }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.HEAD))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, present(equalTo("{'some-json': 'body'}")))

        assertThat(request.headers, present(hasSize(equalTo(2))))
        assertThat(request.headers, Matchers.headerContains("Authorization: Bearer some_token"))
        assertThat(request.headers, Matchers.headerContains("Content-Type: application/text"))
    }

    @Test
    fun `build HEAD with headers collection blocks Test`() {
        val request = RequestBuilderImpl.build {
            head("https://jstohler.com") {
                "{'some-json': 'body'}"
            }
            headers { listOf("Authorization: Bearer some_token", "Content-Type: application/text") }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.HEAD))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, present(equalTo("{'some-json': 'body'}")))

        assertThat(request.headers, present(hasSize(equalTo(2))))
        assertThat(request.headers, Matchers.headerContains("Authorization: Bearer some_token"))
        assertThat(request.headers, Matchers.headerContains("Content-Type: application/text"))
    }

    @Test
    fun `build HEAD with no value Test`() {
        assertThrows<IllegalArgumentException> { RequestBuilderImpl.build {} }
    }

    @Test
    fun `build HEAD with an empty address and body Test`() {
        assertThrows<IllegalArgumentException> { RequestBuilderImpl.build { head("") { "something" } } }
    }

    @Test
    fun `request HEAD Test`() {

        // Mock Response
        val mockResponse = response().withStatusCode(200)

        // Mocks
        server.`when`(request(), Times.exactly(1)).respond(mockResponse)

        // SUT
        val actual = RequestBuilderImpl.request {
            head("http://localhost:1111") { "some-input-body" }
        }

        // Verification
        server.verify(request(), VerificationTimes.once())

        // Validation
        assertThat(actual, present())
        assertThat(actual.statusCode, equalTo(200))
        assertThat(actual.response, present(isEmptyString))
        assertThat(actual.request.method(), equalTo("HEAD"))
        assertThat(actual.request.uri().toASCIIString(), equalTo("http://localhost:1111"))
        assertThat(actual.request.headers().map().size, present(equalTo(0)))

        val actualContentLength = actual.request.bodyPublisher().get().contentLength()
        val expectedContentLength = HttpRequest.BodyPublishers.ofString("some-input-body").contentLength()

        assertThat(actualContentLength, present(equalTo(expectedContentLength)))
    }

    @Test
    fun `request HEAD without body Test`() {

        // Mock Response
        val mockResponse = response().withStatusCode(200)

        // Mocks
        server.`when`(request(), Times.exactly(1)).respond(mockResponse)

        // SUT
        val actual = RequestBuilderImpl.request {
            head("http://localhost:1111")
        }

        // Verification
        server.verify(request(), VerificationTimes.once())

        // Validation
        assertThat(actual, present())
        assertThat(actual.statusCode, equalTo(200))
        assertThat(actual.response, present(isEmptyString))
        assertThat(actual.request.method(), equalTo("HEAD"))
        assertThat(actual.request.uri().toASCIIString(), equalTo("http://localhost:1111"))
        assertThat(actual.request.headers().map().size, present(equalTo(0)))
    }

    @Test
    fun `request HEAD with body and header Test`() {

        // Mock Response
        val mockResponse = response().withStatusCode(200)

        // Mocks
        server.`when`(request(), Times.exactly(1)).respond(mockResponse)

        // SUT
        val actual = RequestBuilderImpl.request {
            head("http://localhost:1111") { "some-input-body" }
            header { "Authorization: Bearer some-token" }
        }

        // Verification
        server.verify(request(), VerificationTimes.once())

        // Validation
        assertThat(actual, present())
        assertThat(actual.statusCode, equalTo(200))
        assertThat(actual.response, present(isEmptyString))
        assertThat(actual.request.method(), equalTo("HEAD"))
        assertThat(actual.request.uri().toASCIIString(), equalTo("http://localhost:1111"))

        val actualHeaderMap = actual.request.headers().map()

        assertThat(actualHeaderMap, present(Matchers.headerSize(equalTo(1))))
        assertThat(actualHeaderMap, present(Matchers.containsKey("Authorization")))
        assertThat(actualHeaderMap["Authorization"], present(Matchers.containsValue("Bearer some-token")))

        val actualContentLength = actual.request.bodyPublisher().get().contentLength()
        val expectedContentLength = HttpRequest.BodyPublishers.ofString("some-input-body").contentLength()

        assertThat(actualContentLength, present(equalTo(expectedContentLength)))
    }

    @Test
    fun `request HEAD with Body and multiple header Test`() {

        // Mock Response
        val mockResponse = response().withStatusCode(200)

        // Mocks
        server.`when`(request(), Times.exactly(1)).respond(mockResponse)

        // SUT
        val actual = RequestBuilderImpl.request {
            head("http://localhost:1111") { "some-input-body" }
            header { "Authorization: Bearer some-token" }
            header { "Authorization: Bearer some-token-2" }
            header { "Accept-Encoding: *" }
        }

        // Verification
        server.verify(request(), VerificationTimes.once())

        // Validation
        assertThat(actual, present())
        assertThat(actual.statusCode, equalTo(200))
        assertThat(actual.response, present(isEmptyString))
        assertThat(actual.request.method(), equalTo("HEAD"))
        assertThat(actual.request.uri().toASCIIString(), equalTo("http://localhost:1111"))

        val actualHeaderMap = actual.request.headers().map()

        assertThat(actualHeaderMap, present(Matchers.headerSize(equalTo(2))))
        assertThat(actualHeaderMap, present(Matchers.containsKey("Authorization")))
        assertThat(actualHeaderMap, present(Matchers.containsKey("Accept-Encoding")))
        assertThat(actualHeaderMap["Authorization"], present(Matchers.containsValue("Bearer some-token")))
        assertThat(actualHeaderMap["Authorization"], present(Matchers.containsValue("Bearer some-token-2")))
        assertThat(actualHeaderMap["Accept-Encoding"], present(Matchers.containsValue("*")))

        val actualContentLength = actual.request.bodyPublisher().get().contentLength()
        val expectedContentLength = HttpRequest.BodyPublishers.ofString("some-input-body").contentLength()

        assertThat(actualContentLength, present(equalTo(expectedContentLength)))
    }

    @Test
    fun `request HEAD with Body and headers Test`() {

        // Mock Response
        val mockResponse = response().withStatusCode(600)

        // Mocks
        server.`when`(request(), Times.exactly(1)).respond(mockResponse)

        // SUT
        val actual = RequestBuilderImpl.request {
            head("http://localhost:1111") { "some-input-body" }
            headers {
                listOf(
                    "Authorization: Bearer some-token",
                    "Authorization: Bearer some-token-2",
                    "Accept-Encoding: *"
                )
            }
        }

        // Verification
        server.verify(request(), VerificationTimes.once())

        // Validation
        assertThat(actual, present())
        assertThat(actual.statusCode, equalTo(600))
        assertThat(actual.response, present(isEmptyString))
        assertThat(actual.request.method(), equalTo("HEAD"))
        assertThat(actual.request.uri().toASCIIString(), equalTo("http://localhost:1111"))

        val actualHeaderMap = actual.request.headers().map()

        assertThat(actualHeaderMap, present(Matchers.headerSize(equalTo(2))))
        assertThat(actualHeaderMap, present(Matchers.containsKey("Authorization")))
        assertThat(actualHeaderMap, present(Matchers.containsKey("Accept-Encoding")))
        assertThat(actualHeaderMap["Authorization"], present(Matchers.containsValue("Bearer some-token")))
        assertThat(actualHeaderMap["Authorization"], present(Matchers.containsValue("Bearer some-token-2")))
        assertThat(actualHeaderMap["Accept-Encoding"], present(Matchers.containsValue("*")))

        val actualContentLength = actual.request.bodyPublisher().get().contentLength()
        val expectedContentLength = HttpRequest.BodyPublishers.ofString("some-input-body").contentLength()

        assertThat(actualContentLength, present(equalTo(expectedContentLength)))
    }
}