package command.implementations

import command.*
import requests.AddRequest

class AddCommand : Command {
    override val info: String
        get() = "добавить новый элемент в коллекцию (поля элемента указываются на отдельных строках)"

    override val argumentValidator = ArgumentValidator(listOf(ArgumentType.ORGANIZATION))

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            AddRequest(args.organizations[0]),
            "Запрос на добавление элемента в коллекцию отправлен"
        )
    }
}