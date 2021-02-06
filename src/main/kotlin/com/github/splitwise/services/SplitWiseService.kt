package com.github.splitwise.services

import com.github.splitwise.models.Settlement
import com.github.splitwise.models.User
import com.github.splitwise.stores.SettlementStore
import com.github.splitwise.transactions.ITransaction
import com.github.splitwise.transactions.Transaction
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class SplitWiseService(
        private val settlementStore: SettlementStore,
        transactions: List<ITransaction>
) {
    private val transactionMap = ConcurrentHashMap<Transaction, ITransaction>()
    init {
        transactions.forEach {
            transactionMap[it.type] = it
        }
    }

    fun update(fromUser: User,
              totalAmount: Double,
              dividedAmong: Int,
              toUsers: List<User>,
              transaction: Transaction,
              weights: List<Double>) {
        (listOf(fromUser) + toUsers).forEach {
            if (!settlementStore.doesUserExists(it)) {
                println("${it.name} does not exist")
                return
            }
        }
        if (dividedAmong == toUsers.size && dividedAmong == weights.size) {
            val settlements = getSettlementsTobeUpdated(transaction,
                    fromUser, toUsers, dividedAmong, totalAmount, weights)
            settlements?.let { updateSettlements(it) }
            settlementStore.displayState()
        }
        else {
            println("number of parameters do not match $dividedAmong, ${toUsers.size}, ${weights.size}")
            return
        }
    }

    private fun updateSettlements(settlements: List<Settlement>) {
        settlements.forEach {
            val reversedSettlement = settlementStore.getSettlement(it.toUser, it.fromUser)
            when {
                reversedSettlement.amount == 0.0 -> {
                    settlementStore.updateSettlement(it.fromUser, it.toUser, it.amount)
                }
                reversedSettlement.amount > it.amount -> {
                    val updatedSettlementAmount = reversedSettlement.amount - it.amount
                    settlementStore.updateSettlement(it.fromUser, it.toUser, 0.0)
                    settlementStore.updateSettlement(it.toUser, it.fromUser, updatedSettlementAmount)
                }
                reversedSettlement.amount < it.amount -> {
                    val updatedSettlementAmount = it.amount - reversedSettlement.amount
                    settlementStore.updateSettlement(it.toUser, it.fromUser, 0.0)
                    settlementStore.updateSettlement(it.fromUser, it.toUser, updatedSettlementAmount)
                }
                // reversedSettlement.amount == it.amount
                else -> {
                    settlementStore.updateSettlement(it.toUser, it.fromUser, 0.0)
                    settlementStore.updateSettlement(it.fromUser, it.toUser, 0.0)
                }
            }
        }
    }

    private fun getSettlementsTobeUpdated(transaction: Transaction,
                                          fromUser: User,
                                          toUsers: List<User>,
                                          dividedAmong: Int,
                                          totalAmount: Double,
                                          weights: List<Double>): List<Settlement>? {
        return if (transaction == Transaction.EQUAL) {
            this.transactionMap[Transaction.PERCENT]?.doSettlement(
                    fromUser,
                    toUsers,
                    (1..dividedAmong).map { (100 * 1.0) / dividedAmong },
                    totalAmount,
                    dividedAmong
            )
        } else {
            this.transactionMap[transaction]?.doSettlement(
                    fromUser,
                    toUsers,
                    weights,
                    totalAmount,
                    dividedAmong
            )
        }
    }
}
