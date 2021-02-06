package com.github.splitwise

import com.github.splitwise.converters.TransactionTypeConverter
import com.github.splitwise.converters.UserConverter
import com.github.splitwise.models.User
import com.github.splitwise.services.SplitWiseService
import com.github.splitwise.transactions.Transaction
import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import javax.inject.Inject

@Command(name = "splitwise", description = ["..."],
    mixinStandardHelpOptions = true, subcommands = [AddUserCommand::class])
class SplitwiseCommand : Runnable {

    @Inject
    private lateinit var splitWiseService: SplitWiseService

    @Option(names = ["-v", "--verbose"], description = ["..."])
    private var verbose : Boolean = false

    @Parameters(index = "0", converter = [UserConverter::class])
    private lateinit var fromUser: User

    @Parameters(index = "1")
    private var totalAmount: Double = 0.0

    @Parameters(index = "2")
    private var divideAmong: Int = 0

    @Parameters(index = "3", converter = [UserConverter::class], split = ",")
    private lateinit var toUsers :List<User>

    @Parameters(index = "4", converter = [TransactionTypeConverter::class])
    private lateinit var transactionType :Transaction

    @Parameters(index = "5", split = ",", defaultValue = "")
    private lateinit var weights :List<Double>

    override fun run() {
        splitWiseService.update(fromUser, totalAmount, divideAmong, toUsers, transactionType, weights)
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(SplitwiseCommand::class.java, *args)
        }
    }
}
