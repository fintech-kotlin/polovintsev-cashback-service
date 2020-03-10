package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Client

@Component
class ClientServiceImpl(
    var restTemplate: RestTemplate
): ClientService {

    @Value("\${rest.api.client}")
    lateinit var uri: String

    override fun getClient(id: String): Client =
        restTemplate.getForObject(uri, Client::class.java, mapOf("id" to id))
}