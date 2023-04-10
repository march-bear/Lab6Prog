package command.implementations

import IdManager
import command.*
import iostreamers.Messenger
import iostreamers.TextColor
import requests.AddIfMaxRequest

class AddIfMaxCommand : Command {
    override val info: String
        get() = "добавить новый элемент в коллекцию, если его значение " +
                "превышает значение наибольшего элемента этой коллекции"

    override val argumentValidator: ArgumentValidator = ArgumentValidator(listOf(ArgumentType.ORGANIZATION))

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            AddIfMaxRequest(args.organizations[0]),
            message = "Запрос на добавление элемента отправлен"
        )

    }
}