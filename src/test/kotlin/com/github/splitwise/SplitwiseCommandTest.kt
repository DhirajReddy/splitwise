package com.github.splitwise

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import picocli.CommandLine

class SplitwiseCommandTest {

    @Test
    fun testWithCommandLineOption() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val baos = ByteArrayOutputStream()
        System.setOut(PrintStream(baos))

        val args = arrayOf("-v")
        PicocliRunner.run(SplitwiseCommand::class.java, ctx, *args)

        //Assertions.assertTrue(baos.toString().contains("Hi!"))

        ctx.close()
    }

    @Test
    fun addUserTest() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val baos = ByteArrayOutputStream()
        System.setOut(PrintStream(baos))

        val args = "-n u3 -m u3".split(" ").toTypedArray()
        PicocliRunner.run(AddUserCommand::class.java, ctx, *args)
        ctx.close()
    }

    @Test
    fun exactTest() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val baos = ByteArrayOutputStream()
        System.setOut(PrintStream(baos))

        var args = "u1 300 3 u1,u2,u3 EXACT 100,100,100".split(" ").toTypedArray()
        PicocliRunner.run(SplitwiseCommand::class.java, ctx, *args)

        args = "u2 400 2 u1,u3 EXACT 100,300".split(" ").toTypedArray()
        PicocliRunner.run(SplitwiseCommand::class.java, ctx, *args)

        args = "u3 1000 3 u1,u2,u3 PERCENT 30,20,50".split(" ").toTypedArray()
        PicocliRunner.run(SplitwiseCommand::class.java, ctx, *args)
        ctx.close()
    }
}
