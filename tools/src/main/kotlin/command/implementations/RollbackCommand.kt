package command.implementations

import command.*
import commandcallgraph.RequestGraph
import iostreamers.Messenger
import iostreamers.TextColor
import requests.RollbackRequest

class RollbackCommand: Command {
    override val info: String
        get() = "вернуть коллекцию к состоянию по id запроса. Для полного отката вводится ${RequestGraph.ROOT_NAME}"

    override val argumentValidator: ArgumentValidator = ArgumentValidator(listOf(ArgumentType.STRING))

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            RollbackRequest(args.primitiveTypeArguments!![0]),
            "Запрос на откат коллекции отправлен"
        )
    }
}