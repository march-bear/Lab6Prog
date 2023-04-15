package command.implementations

import CommandManager
import OrganizationFactory
import command.*
import exceptions.InvalidFieldValueException
import exceptions.ScriptException
import iostreamers.Reader
import requests.ExecuteCommandsRequest
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.*


class ExecuteScriptCommand(
    private val commandManager: CommandManager,
) : Command {
    override val info: String
        get() = "считать и исполнить скрипт из указанного файла (название файла указывается на после команды)"

    override val argumentValidator = ArgumentValidator(listOf(ArgumentType.STRING))

    private val scriptFiles = Stack<String>()

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        val commandList: LinkedList<Pair<Command, CommandArgument>> = LinkedList()
        val fileName: String = args.primitiveTypeArguments?.get(0)!!

        try {
            addCommandsFromFile(fileName, commandList)
        } catch (ex: ScriptException) {
            return CommandResult(false,
                message = ex.message ?: ex.toString()
            )
        }

        return CommandResult(
            true,
            request = ExecuteCommandsRequest(commandList),
            message = "Запрос на исполнение скрипта отправлен"
        )
    }

    private fun addCommandsFromFile(fileName: String, commandList: LinkedList<Pair<Command, CommandArgument>>) {
        /*if (fileName in this.scriptFiles) {
            var message = "Обнаружен циклический вызов скрипта:"

            for (i in scriptFiles.indexOf(fileName) until scriptFiles.size)
                message += "\n ${scriptFiles[i]} ->"
            throw ScriptException("$message $fileName")
        }

        scriptFiles.add(fileName)
        val script: String
        try {
            val inputStreamReader = InputStreamReader(FileInputStream(fileName))
            script = inputStreamReader.readText()
            inputStreamReader.close()
        } catch (ex: FileNotFoundException) {
            throw ScriptException("Ошибка во время открытия файла $fileName: ${ex.message}")
        }
        val reader = Reader(Scanner(script))
        var commandData = reader.readCommand()

        while (commandData != null) {
            val (commandName, commandArguments) = commandData
            val command: Command = commandManager.getCommand(commandName)
                ?: throwNestedScriptException(fileName, reader.lineCounter, "$commandName: команда не найдена")

            for (i in 1..commandArguments.organizationLimit) {
                try {
                    commandArguments.organizations.add(OrganizationFactory(reader).newOrganizationFromInput())
                } catch (ex: InvalidFieldValueException) {
                    throwNestedScriptException(fileName, reader.lineCounter,
                        "Ошибка во время считывания аргумента для команды")
                }
            }
            if (command::class == this::class) {
                if (this.scriptFiles.size > MAXIMUM_NESTED_SCRIPTS_CALLS)
                    throwNestedScriptException(fileName, reader.lineCounter,
                        "Превышено максимальное количество вложенных вызовов скриптов: $MAXIMUM_NESTED_SCRIPTS_CALLS")
                try {
                    addCommandsFromFile(commandArguments.primitiveTypeArguments?.get(0) ?: "", commandList)
                } catch (ex: FileNotFoundException) {
                    throwNestedScriptException(fileName, reader.lineCounter, ex.message)
                } catch (ex: ScriptException) {
                    throwNestedScriptException(fileName, reader.lineCounter, ex.message)
                }
            } else {
                commandList.add(Pair(command, commandArguments))
            }
            commandData = reader.readCommand()
        }

        scriptFiles.pop()*/
    }

    companion object {
        private const val MAXIMUM_NESTED_SCRIPTS_CALLS: Int = 10

        private fun throwNestedScriptException(
            fileName: String,
            lineNumber: ULong,
            message: String?
        ): Nothing {
            throw ScriptException("${message}\n<- Ошибка во время проверки скрипта $fileName, строка $lineNumber")
        }
    }
}