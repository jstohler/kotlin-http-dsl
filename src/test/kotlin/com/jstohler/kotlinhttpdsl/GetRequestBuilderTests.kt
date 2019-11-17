package com.jstohler.kotlinhttpdsl

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import org.junit.jupiter.api.Test

class GetRequestBuilderTests {

    @Test
    fun `build GET Test`() {
        val request = RequestBuilder.build {
            get { "https://jstohler.com" }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.GET))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, absent())

        assertThat(request.headers, present(isEmpty))
    }

    @Test
    fun `build GET with header Test`() {
        val request = RequestBuilder.build {
            get { "https://jstohler.com" }
            header { "Authorization: Bearer some_token" }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.GET))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, absent())
        assertThat(request.headers, present(hasSize(equalTo(1))))
        assertThat(request.headers, headerContains("Authorization: Bearer some_token"))
    }

    @Test
    fun `build GET with multiple header blocks Test`() {
        val request = RequestBuilder.build {
            get { "https://jstohler.com" }
            header { "Authorization: Bearer some_token" }
            header { "Content-Type: application/text" }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.GET))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, absent())

        assertThat(request.headers, present(hasSize(equalTo(2))))
        assertThat(request.headers, headerContains("Authorization: Bearer some_token"))
        assertThat(request.headers, headerContains("Content-Type: application/text"))
    }

    @Test
    fun `build GET with headers collection blocks Test`() {
        val request = RequestBuilder.build {
            get { "https://jstohler.com" }
            headers { listOf("Authorization: Bearer some_token", "Content-Type: application/text") }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.GET))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, absent())

        assertThat(request.headers, present(hasSize(equalTo(2))))
        assertThat(request.headers, headerContains("Authorization: Bearer some_token"))
        assertThat(request.headers, headerContains("Content-Type: application/text"))
    }

    @Test
    fun `build GET with headers and body blocks Test`() {
        val request = RequestBuilder.build {
            get { "https://jstohler.com" }
            header { "Authorization: Bearer some_token" }
            body { "{'some-json': 'body'}" }
        }

        assertThat(request, present())
        assertThat(request.httpVerb, equalTo(HttpVerb.GET))
        assertThat(request.target, equalTo("https://jstohler.com"))
        assertThat(request.body, present(equalTo("{'some-json': 'body'}")))

        assertThat(request.headers, present(hasSize(equalTo(1))))
        assertThat(request.headers, headerContains("Authorization: Bearer some_token"))
    }

    companion object {
        fun headerContains(expected: String) = Matcher(Set<String>::contains, expected)
    }
}