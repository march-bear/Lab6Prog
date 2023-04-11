package command.implementations

import command.Command
import command.CommandArgument
import command.CommandResult
import requests.SumOfEmployeesCountRequest

class SumOfEmployeesCountCommand : Command {
    override val info: String
        get() = "вывести сумму значений поля employeesCount для всех элементов коллекции"

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(
            true,
            SumOfEmployeesCountRequest(),
            "Запрос на получение суммы значений поля employeesCount для всех элементов коллекции отправлен",
        )
    }
}