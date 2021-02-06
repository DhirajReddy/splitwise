package com.github.splitwise.converters

import com.github.splitwise.transactions.Transaction
import picocli.CommandLine

class TransactionTypeConverter: CommandLine.ITypeConverter<Transaction> {
    override fun convert(value: String?): Transaction {
        if (value.isNullOrEmpty()) {
            throw Exception("transaction should not be empty")
        }
        return Transaction.valueOf(value.toUpperCase())
    }
}
