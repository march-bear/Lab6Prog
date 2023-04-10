package command.implementations

import command.Command
import command.CommandArgument
import command.CommandResult

class ExitCommand : Command {
    override val info: String
        get() = "завершить программу (без сохранения в файл)"

    override fun execute(args: CommandArgument): CommandResult {
        argumentValidator.check(args)

        return CommandResult(true)
    }
}