package com.github.splitwise.services

import com.github.splitwise.stores.SettlementStore
import javax.inject.Singleton

@Singleton
class RegistrationService(private val settlementStore: SettlementStore) {
    fun addUser(name: String, mobileNumber: String): Boolean {
        return settlementStore.addUser(name, mobileNumber)
    }
}
