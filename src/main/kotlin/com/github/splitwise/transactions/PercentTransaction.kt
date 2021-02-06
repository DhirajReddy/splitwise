package com.github.splitwise.transactions

import com.github.splitwise.models.Settlement
import com.github.splitwise.models.User
import com.github.splitwise.stores.SettlementStore
import javax.inject.Singleton

@Singleton
class PercentTransaction(private val settlementStore: SettlementStore): ITransaction {
    override val type = Transaction.PERCENT
    override fun doSettlement(fromUser: User,
                              toUsers: List<User>,
                              weights: List<Double>,
                              totalAmount: Double,
                              dividedAmong: Int): List<Settlement> {

        val toUsersAndWeights = if (toUsers.contains(fromUser)) {
            val fromUserWeightIndex = toUsers.indexOf(fromUser)
            Pair(toUsers.filter { it != fromUser }, weights.filterIndexed { index, _ -> index != fromUserWeightIndex })
        }
        else {
            Pair(toUsers, weights)
        }

        val settlements = toUsersAndWeights.first.mapIndexed { index, user ->
            Settlement(user, fromUser, (totalAmount * (toUsersAndWeights.second[index]/100.0)))
        }
        return settlements
    }
}
