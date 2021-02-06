package com.github.splitwise.models

data class User(val name: String, val mobileNumber: String) {
    override fun equals(other: Any?): Boolean {
        return other is User && other.mobileNumber == mobileNumber
    }
}
