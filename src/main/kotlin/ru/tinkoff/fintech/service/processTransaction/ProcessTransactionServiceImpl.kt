package ru.tinkoff.fintech.service.processTransaction

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.model.Transaction

@Service
class ProcessTransactionServiceImpl : ProcessTransactionService {

    override fun process(transaction: Transaction) {
        println(transaction)
    }
}