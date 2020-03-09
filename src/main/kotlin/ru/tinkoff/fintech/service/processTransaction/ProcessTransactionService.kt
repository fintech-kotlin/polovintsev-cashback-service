package ru.tinkoff.fintech.service.processTransaction

import ru.tinkoff.fintech.model.Transaction

interface ProcessTransactionService {

    fun process(transaction: Transaction)
}