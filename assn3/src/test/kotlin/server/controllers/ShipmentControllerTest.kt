package server.controllers

import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import models.viewmodels.ShippingUpdateViewModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import io.ktor.client.request.*
import io.ktor.client.statement.*
import server.repositories.ShipmentRepository
import org.junit.jupiter.api.BeforeEach

class ShipmentControllerTest {

    private fun Application.testModule() {
        install(ContentNegotiation) { json() }
        install(CallLogging)
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondText("Internal server error", status = HttpStatusCode.InternalServerError)
            }
        }
        routing {
            ShipmentController.register(this)
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    private fun makeViewModel(
        id: String = "test-id",
        updateType: String = "CREATED",
        timestamp: String = Instant.now().toEpochMilli().toString(),
        otherInfo: String? = null
    ) = ShippingUpdateViewModel(updateType, id, timestamp, otherInfo)

    @BeforeEach
    fun setup() {
        ShipmentRepository.clear()
    }

    @Test
    fun `POST shipment create - success`() = testApplication {
        application { testModule() }
        val viewModel = makeViewModel()
        val response = client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), viewModel))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"id\":\"test-id\""))
    }

    @Test
    fun `POST shipment create - conflict if already exists`() = testApplication {
        application { testModule() }
        val viewModel = makeViewModel()
        client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), viewModel))
        }
        val response = client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), viewModel))
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertTrue(response.bodyAsText().contains("Shipment already exists"))
    }

    @Test
    fun `PATCH shipment update - success`() = testApplication {
        application { testModule() }
        val viewModel = makeViewModel()
        client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), viewModel))
        }
        val updateViewModel = makeViewModel(updateType = "SHIPPED")
        val response = client.patch("/shipment/update") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), updateViewModel))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\":\"SHIPPED\""))
    }

    @Test
    fun `PATCH shipment update - not found`() = testApplication {
        application { testModule() }
        val viewModel = makeViewModel(id = "not-exist")
        val response = client.patch("/shipment/update") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), viewModel))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Shipment not found"))
    }

    @Test
    fun `GET shipment by id - success`() = testApplication {
        application { testModule() }
        val viewModel = makeViewModel()
        client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), viewModel))
        }
        val response = client.get("/shipment/test-id")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"id\":\"test-id\""))
    }

    @Test
    fun `GET shipment by id - not found`() = testApplication {
        application { testModule() }
        val response = client.get("/shipment/does-not-exist")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Shipment not found"))
    }

    @Test
    fun `POST shipment create - invalid JSON returns error`() = testApplication {
        application { testModule() }
        val response = client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody("{ invalid json }")
        }
        // Depending on StatusPages config, this may be 500 or 400
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Internal server error"))
    }

    @Test
    fun `POST shipment create - missing required field returns error`() = testApplication {
        application { testModule() }
        // Missing id
        val invalidJson = """{"updateType":"CREATED","timestamp":"1234567890"}"""
        val response = client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(invalidJson)
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Internal server error"))
    }

    @Test
    fun `POST shipment create - invalid timestamp returns error`() = testApplication {
        application { testModule() }
        val invalidTimestamp = makeViewModel(timestamp = "not_a_number")
        val response = client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), invalidTimestamp))
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Internal server error"))
    }

    @Test
    fun `PATCH shipment update - unknown update type returns error`() = testApplication {
        application { testModule() }
        // Ensure the shipment exists before updating
        val viewModel = makeViewModel()
        client.post("/shipment/create") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), viewModel))
        }
        val badUpdate = makeViewModel(updateType = "UNKNOWN_TYPE")
        val response = client.patch("/shipment/update") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ShippingUpdateViewModel.serializer(), badUpdate))
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertTrue(response.bodyAsText().contains("Internal server error"))
    }
}