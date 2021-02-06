package com.github.splitwise.transactions

import com.github.splitwise.models.Settlement
import com.github.splitwise.models.User

interface ITransaction {
    val type: Transaction
    fun doSettlement(fromUser: User,
                     toUsers: List<User>,
                     weights: List<Double>,
                     totalAmount: Double,
                     dividedAmong: Int): List<Settlement>
}
