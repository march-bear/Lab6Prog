package command.implementations

import command.*
import requests.SaveRequest

class SaveCommand : Command {
    override val info: String
        get() = "сохранить коллекцию в файл"

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(true, SaveRequest(), "Запрос на сохранение коллекции отправлен")
    }
}