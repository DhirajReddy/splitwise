package com.github.splitwise.stores

import com.github.splitwise.models.Settlement
import com.github.splitwise.models.User
import io.micronaut.context.annotation.Value
import java.io.File
import java.io.FileWriter
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class SettlementStore(
        @Value("\${micronaut.application.usersfile}")
        private val userFilePath: String,
        @Value("\${micronaut.application.settlementsfile}")
        private val settlementsFilePath: String,
) {

    private val allUsers: MutableList<User> = mutableListOf()

    private val allSettlements: MutableList<Settlement> = mutableListOf()

    init {
        val usersFile = File(userFilePath)
        if (usersFile.exists()) {
            usersFile.useLines { allLines ->
                allLines.forEach {
                    val name = it.split("\t").first()
                    val mobileNumber = it.split("\t").last()
                    this.allUsers.add(User(name, mobileNumber))
                }
            }
        }

        val settlementsFile = File(settlementsFilePath)
        if(settlementsFile.exists()) {
            settlementsFile.useLines { settlementLines ->
                settlementLines.forEach { line ->
                    val lineContents = line.split("\t")
                    val fromUser = allUsers.first { it.mobileNumber == lineContents[0] }
                    val toUser = allUsers.first { it.mobileNumber == lineContents[1] }
                    val amount = lineContents[2].toDouble()
                    this.allSettlements.add(Settlement(fromUser, toUser, amount))
                }
            }
        }
    }

    fun addUser(name: String, mobileNumber: String): Boolean {
        val userTobeAdded = User(name, mobileNumber)
        if (doesUserExists(userTobeAdded)) {
            return false
        }
        allUsers.add(userTobeAdded)
        val fw = File(userFilePath)
        fw.appendText("${userTobeAdded.name}\t${userTobeAdded.mobileNumber}\n")
        val allUsersSize = allUsers.size
        val sw = File(settlementsFilePath)
        (0 until allUsersSize).forEach {
            val existingUser = allUsers.filter { au -> au.mobileNumber != userTobeAdded.mobileNumber }[it]
            val newSettlements = listOf(Settlement(userTobeAdded, existingUser, 0.0),
                    Settlement(existingUser, userTobeAdded, 0.0))
            allSettlements.addAll(newSettlements)
            newSettlements.forEach { settlement ->
                sw.appendText("${settlement.fromUser.mobileNumber}\t${settlement.toUser.mobileNumber}\t${settlement.amount}\n")
            }
        }

        return true
    }

    fun doesUserExists(user: User): Boolean {
        return allUsers.any { it == user }
    }

    fun getUser(mobileNumber: String): User? {
        return allUsers.firstOrNull { it.mobileNumber == mobileNumber }
    }

    fun updateSettlement(fromUser: User, toUser: User, amount: Double) {
        with(allSettlements.first { it.fromUser == fromUser && it.toUser == toUser }) {
            this.amount = amount
        }
        FileWriter(settlementsFilePath).use { it.write("") }
        FileWriter(settlementsFilePath).use { w ->
            allSettlements.forEach { settlement ->
                w.appendLine("${settlement.fromUser.mobileNumber}\t${settlement.toUser.mobileNumber}\t${settlement.amount}")
            }
        }
    }

    fun getSettlement(fromUser: User, toUser: User): Settlement {
        return allSettlements.first { it.fromUser == fromUser && it.toUser == toUser }
    }

    fun displayState() {
        allSettlements.filter { it.amount > 0.0 }.forEach {
            println("${it.fromUser.name} owes ${it.toUser.name} ${it.amount.roundToInt()}")
        }
    }
}
