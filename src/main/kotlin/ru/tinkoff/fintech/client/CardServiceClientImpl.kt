package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Card


@Component
class CardServiceClientImpl(
    var restTemplate: RestTemplate
): CardServiceClient {

    @Value("\${rest.api.card}")
    lateinit var uri: String

    override fun getCard(id: String): Card =
        restTemplate.getForObject(uri, Card::class.java, mapOf("id" to id))
}