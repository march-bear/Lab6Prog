package command.implementations

import command.Command
import command.CommandArgument
import command.CommandResult
import requests.GetUniquePostalAddressRequest

class PrintUniquePostalAddressCommand : Command {
    override val info: String
        get() = "вывести уникальные значения поля postalAddress всех элементов в коллекции"

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            GetUniquePostalAddressRequest(),
            "запрос на получение уникальных значений поля postalAddress всех элементов в коллекции отправлен",
        )
    }
}