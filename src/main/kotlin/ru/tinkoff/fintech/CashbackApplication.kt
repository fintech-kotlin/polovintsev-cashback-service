package ru.tinkoff.fintech

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class CashbackApplication

fun main(args: Array<String>) {
    SpringApplication.run(CashbackApplication::class.java, *args)
}