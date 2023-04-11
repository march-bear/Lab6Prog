package command.implementations

import command.Command
import command.CommandArgument
import command.CommandResult
import java.lang.ref.SoftReference

class ShowFieldRequirementsCommand : Command {
    override val info: String
        get() = "вывести требования к полям класса Organization"

    override fun execute(args: CommandArgument): CommandResult {
        return CommandResult(true, message = Organization.fieldRequirements)
    }

}