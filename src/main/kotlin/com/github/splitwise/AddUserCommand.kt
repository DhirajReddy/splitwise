package com.github.splitwise

import com.github.splitwise.services.RegistrationService
import picocli.CommandLine.Option
import picocli.CommandLine.Command
import javax.inject.Inject

@Command(name = "add")
class AddUserCommand: Runnable {
    @Inject
    private lateinit var registrationService: RegistrationService

    @Option(names = ["-n", "--name"], required = true)
    private lateinit var name: String

    @Option(names = ["-m", "--mobile"], required = true)
    private lateinit var mobile: String

    override fun run() {
        registrationService.addUser(name, mobile)
    }
}
