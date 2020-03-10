package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class NotificationServiceClientImp(
    var restTemplate: RestTemplate
) : NotificationServiceClient {

    @Value("\${rest.api.notification}")
    lateinit var uri: String

    override fun sendNotification(clientId: String, message: String) {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity = HttpEntity(message, headers)

        restTemplate.exchange(uri, HttpMethod.POST, entity, String::class.java, mapOf("id" to clientId))

    }
}