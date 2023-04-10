package command.implementations

import command.Command
import command.CommandArgument
import command.CommandResult
import iostreamers.Messenger
import iostreamers.TextColor
import requests.ClearRequest

class ClearCommand : Command {
    override val info: String
        get() = "очистить коллекцию"

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)
        return CommandResult(
            true,
            ClearRequest(),
            message = "Запрос на очистку коллекции отправлен"
        )
    }
}