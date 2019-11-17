package com.jstohler.kotlinhttpdsl.builder

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractMockServer {

    lateinit var server: ClientAndServer

    @BeforeAll
    fun before() {
        server = ClientAndServer.startClientAndServer(1111)
    }

    @AfterEach
    fun afterEach() {
        server.reset()
    }

    @AfterAll
    fun after() {
        server.stop()
    }

}