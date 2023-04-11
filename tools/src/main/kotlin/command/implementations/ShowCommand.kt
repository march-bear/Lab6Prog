package command.implementations

import command.Command
import command.CommandArgument
import command.CommandResult
import requests.ShowRequest

class ShowCommand : Command {
    override val info: String
        get() = "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            ShowRequest(),
            message = "Запрос на получение списка описаний элементов коллекции отправлен"
        )
    }
}