package command.implementations

import Organization
import command.*
import iostreamers.Messenger
import iostreamers.TextColor
import requests.UpdateRequest

class UpdateCommand : Command {
    override val info: String
        get() = "обновить значение элемента коллекции, id которого равен заданному"
    override val argumentValidator = ArgumentValidator(listOf(ArgumentType.LONG, ArgumentType.ORGANIZATION))

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        val id: Long = args.primitiveTypeArguments?.get(0)?.toLong() ?: -1
        if (!Organization.idIsValid(id))
            return CommandResult(false, message = "Введенное значение не является id")

        return CommandResult(
            true,
            UpdateRequest(id, args.organizations[0]),
            message = "Запрос на обновление значения элемента коллекции с id $id отправлен"
        )
    }
}