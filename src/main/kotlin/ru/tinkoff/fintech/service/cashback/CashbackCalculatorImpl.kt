package ru.tinkoff.fintech.service.cashback

import ru.tinkoff.fintech.model.TransactionInfo
import java.lang.Double.min
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month


internal const val LOYALTY_PROGRAM_BLACK = "BLACK"
internal const val LOYALTY_PROGRAM_ALL = "ALL"
internal const val LOYALTY_PROGRAM_BEER = "BEER"
internal const val MAX_CASH_BACK = 3000.0
internal const val MCC_SOFTWARE = 5734
internal const val MCC_BEER = 5921
internal val MONTH_WITH_FIRST_LETTER = mapOf(
    Month.JANUARY.value to 'я',
    Month.FEBRUARY.value to 'ф',
    Month.MARCH.value to 'м',
    Month.APRIL.value to 'а',
    Month.MAY.value to 'м',
    Month.JUNE.value to 'и',
    Month.JULY.value to 'и',
    Month.AUGUST.value to 'а',
    Month.SEPTEMBER.value to 'с',
    Month.OCTOBER.value to 'о',
    Month.NOVEMBER.value to 'н',
    Month.DECEMBER.value to 'д'
)



class CashbackCalculatorImpl : CashbackCalculator {

    override fun calculateCashback(transactionInfo: TransactionInfo): Double {
        var cashbackAmount = when(transactionInfo.loyaltyProgramName) {
            LOYALTY_PROGRAM_BLACK -> calculateCashbackBlackProgram(transactionInfo)
            LOYALTY_PROGRAM_ALL -> calculateCashbackAllProgram(transactionInfo)
            LOYALTY_PROGRAM_BEER -> calculateCashbackBeerProgram(transactionInfo)
            else -> 0.0
        }

        if(transactionInfo.transactionSum % 666.0 == 0.0) {
            cashbackAmount += 6.66
        }


        return if(transactionInfo.cashbackTotalValue >= MAX_CASH_BACK) 0.0
            else round(min(
                min(cashbackAmount, MAX_CASH_BACK),
                min(cashbackAmount, MAX_CASH_BACK - transactionInfo.cashbackTotalValue)
            ))
    }


    private fun calculateCashbackBlackProgram(transactionInfo: TransactionInfo): Double
            = calculateCashbackSum(transactionInfo.transactionSum, 1.0)

    private fun calculateCashbackBeerProgram(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            val firstName = firstName.toLowerCase()
            val lastName = lastName.toLowerCase()

            return if(mccCode == MCC_BEER) {
                when {
                    firstName == "олег" && lastName == "олегов" -> calculateCashbackSum(transactionSum, 10.0)
                    firstName == "олег" -> calculateCashbackSum(transactionSum, 7.0)
                    firstName[0] == MONTH_WITH_FIRST_LETTER[LocalDate.now().month.value] -> calculateCashbackSum(transactionSum, 5.0)
                    firstName[0] == MONTH_WITH_FIRST_LETTER[LocalDate.now().minusMonths(1).month.value] -> calculateCashbackSum(transactionSum, 3.0)
                    firstName[0] == MONTH_WITH_FIRST_LETTER[LocalDate.now().plusMonths(1).month.value] -> calculateCashbackSum(transactionSum, 3.0)
                    else -> calculateCashbackSum(transactionSum, 2.0)
                }
            } else 0.0
        }
    }

    private fun calculateCashbackAllProgram(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            return when {
                mccCode == MCC_SOFTWARE && isPalindrome(transactionSum)
                    -> calculateCashbackSum(transactionSum, lcm(firstName.length, lastName.length) / 1000.0)
                else -> 0.0
            }
        }
    }

    private fun calculateCashbackSum(amount: Double, percent: Double): Double = (amount * percent) / 100.0

    private fun isPalindrome(transactionAmount: Double): Boolean {
        val transactionAmountStr = (transactionAmount * 100).toLong().toString()
        val length = transactionAmountStr.length
        var countReplace = 0

        for(i in 0 until length/2) {
            if(transactionAmountStr[i] != transactionAmountStr[length - 1 - i]) {
                countReplace++
            }
        }

        return countReplace <= 1
    }

    private fun lcm(a: Int, b: Int): Int  = a / gcd(a, b) * b

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    private fun round(amount: Double): Double = BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN).toDouble()

 }
