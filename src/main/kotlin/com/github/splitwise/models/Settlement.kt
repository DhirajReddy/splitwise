package com.github.splitwise.models

data class Settlement(val fromUser: User, val toUser: User, var amount: Double)
