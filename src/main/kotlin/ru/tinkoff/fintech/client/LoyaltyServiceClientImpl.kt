package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.LoyaltyProgram

@Component
class LoyaltyServiceClientImpl(
    var restTemplate: RestTemplate
): LoyaltyServiceClient {

    @Value("\${rest.api.program}")
    lateinit var uri: String

    override fun getLoyaltyProgram(id: String): LoyaltyProgram =
        restTemplate.getForObject(uri, LoyaltyProgram::class.java, mapOf("id" to id))
}