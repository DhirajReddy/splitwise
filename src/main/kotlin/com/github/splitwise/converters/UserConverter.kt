package com.github.splitwise.converters

import com.github.splitwise.models.User
import com.github.splitwise.stores.SettlementStore
import picocli.CommandLine
import javax.inject.Inject

class UserConverter: CommandLine.ITypeConverter<User> {

    @Inject
    private lateinit var settlementStore: SettlementStore

    override fun convert(value: String?): User {
        if (value.isNullOrEmpty()) {
            throw Exception("user should not be empty")
        }
        return settlementStore.getUser(value) ?: throw Exception("user does not exist")
    }
}
