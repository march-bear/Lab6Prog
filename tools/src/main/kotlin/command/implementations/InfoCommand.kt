package command.implementations

import command.Command
import command.CommandArgument
import command.CommandResult
import requests.InfoRequest

class InfoCommand : Command {
    override val info: String
        get() = "вывести в стандартный поток вывода информацию о коллекции"

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            InfoRequest(),
            message = "Запрос на получение информации о коллекции отправлен",
        )
    }
}