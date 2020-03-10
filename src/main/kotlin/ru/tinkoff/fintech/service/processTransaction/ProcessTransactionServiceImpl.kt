package ru.tinkoff.fintech.service.processTransaction

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.client.CardServiceClient
import ru.tinkoff.fintech.client.ClientService
import ru.tinkoff.fintech.client.LoyaltyServiceClient
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity
import ru.tinkoff.fintech.db.repository.LoyaltyPaymentRepository
import ru.tinkoff.fintech.model.*
import ru.tinkoff.fintech.service.cashback.CashbackCalculator
import ru.tinkoff.fintech.service.notification.NotificationService
import java.time.LocalDateTime

@Service
class ProcessTransactionServiceImpl(
    val cardServiceClient: CardServiceClient,
    val clientService: ClientService,
    val loyaltyServiceClient: LoyaltyServiceClient,
    val cashbackCalculator: CashbackCalculator,
    val loyaltyPaymentRepository: LoyaltyPaymentRepository,
    val notificationService: NotificationService
) : ProcessTransactionService {

    @Value("\${sing}")
    lateinit var sing: String

    override fun process(transaction: Transaction) {
        val card: Card = cardServiceClient.getCard(transaction.cardNumber)
        val client: Client = clientService.getClient(card.client)
        val program: LoyaltyProgram = loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)
        val loyaltyPayment: Set<LoyaltyPaymentEntity> = loyaltyPaymentRepository
            .findAllBySignAndCardIdAndDateTimeAfter(sing, card.id, transaction.time)

        val transactionInfo = TransactionInfo(
            loyaltyProgramName = program.name,
            transactionSum = transaction.value,
            cashbackTotalValue = loyaltyPayment.sumByDouble { it.value },
            mccCode = transaction.mccCode,
            clientBirthDate = client.birthDate,
            firstName = client.firstName,
            middleName = client.middleName,
            lastName = client.lastName
        )

        val cashbackAmount = cashbackCalculator.calculateCashback(transactionInfo)

        loyaltyPaymentRepository.save(LoyaltyPaymentEntity(
            value = cashbackAmount,
            cardId = card.id,
            sign = sing,
            transactionId = transaction.transactionId,
            dateTime = transaction.time
        ))

        notificationService.sendNotification(
            client.id,
            NotificationMessageInfo(
                name = client.firstName,
                cardNumber = card.cardNumber,
                cashback = cashbackAmount,
                transactionSum = transaction.value,
                category = program.name,
                transactionDate = transaction.time
            )
        )
    }
}