package command.implementations

import CollectionType
import command.*
import requests.ChangeCollectionTypeRequest

class ChangeCollectionTypeCommand: Command {
    override val info: String
        get() = "изменить тип коллекции (QUEUE/SET/LIST)"

    override val argumentValidator: ArgumentValidator = ArgumentValidator(listOf(ArgumentType.STRING))

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        val request = when (args.primitiveTypeArguments!![0].lowercase()) {
            "queue" -> ChangeCollectionTypeRequest(CollectionType.QUEUE)
            "list" -> ChangeCollectionTypeRequest(CollectionType.LIST)
            "set" -> ChangeCollectionTypeRequest(CollectionType.SET)
            else -> return CommandResult(
                false,
                message = "Заданный тип коллекции не найден",
            )
        }

        return CommandResult(
            true,
            request,
            message = "Запрос на изменение типа коллекции отправлен"
        )
    }
}