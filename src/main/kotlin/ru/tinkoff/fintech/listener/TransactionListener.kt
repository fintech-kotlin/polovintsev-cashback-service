package ru.tinkoff.fintech.listener

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.service.processTransaction.ProcessTransactionService


@Component
class TransactionListener(
    var processTransactionService: ProcessTransactionService
) {

    @KafkaListener(topics = ["\${paimentprocessing.kafka.consumer.topic}"], groupId = "\${paimentprocessing.kafka.consumer.groupId}")
    fun onMessage(transaction: Transaction) {
        try {
            processTransactionService.process(transaction)
        } catch (e: Exception) {
            log.error("Error messages: ", e)
        }
    }
    
    companion object {
        private val log = LoggerFactory.getLogger(TransactionListener::class.java)
    }
}


