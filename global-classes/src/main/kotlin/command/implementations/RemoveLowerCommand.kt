package command.implementations

import command.*
import iostreamers.Messenger
import iostreamers.TextColor
import requests.RemoveLowerRequest

/**
 * Класс команды remove_lower для удаления всех элементов коллекции, меньших, чем введенный
 */
class RemoveLowerCommand : Command {
    override val info: String
        get() = "удалить из коллекции все элементы, меньшие, чем заданный"

    override val argumentValidator = ArgumentValidator(listOf(ArgumentType.ORGANIZATION))

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            RemoveLowerRequest(args.organizations[0]),
            message = "Запрос на удаление всех элементов, меньших заданного, отправлен",
        )
    }
}