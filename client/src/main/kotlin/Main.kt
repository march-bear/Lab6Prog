import command.Command
import command.CommandData
import exceptions.InvalidArgumentsForCommandException
import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import org.koin.core.context.startKoin
import java.util.*
import kotlin.NoSuchElementException

fun main(args: Array<String>) {
    val app = startKoin {
        modules(clientCommandManager)
    }

    val commandManager = app.koin.get<CommandManager>()

    Messenger.interactiveModeMessage()
    Messenger.printMessage(
        "\nДобро пожаловать в интерактивный режим! " +
                "Для просмотра доступных команд введите `help`"
    )

    val reader = Reader(Scanner(System.`in`))
    var commandData: CommandData?
    var command: Command?

    do {
        Messenger.inputPrompt(">>>", " ")
        commandData = reader.readCommand()

        if (commandData == null || commandData.name == "")
            continue

        if (commandData.args.needAnOrganization) {
            try {
                commandData.args.setOrganization(OrganizationFactory().newOrganizationFromInput())
            } catch (ex: NoSuchElementException) { break }
        }

        try {
            command = commandManager.getCommand(commandData.name)
            if (command == null) {
                Messenger.printMessage("${commandData.name}: команда не найдена", TextColor.RED)
            } else {
                val (completed, request, message) = command.execute(commandData.args)

                if (completed) {
                    Messenger.printMessage(message, TextColor.BLUE)
                } else {
                    Messenger.printMessage(message, TextColor.RED)
                }
            }
        } catch (e: InvalidArgumentsForCommandException) {
            Messenger.printMessage(e.message, TextColor.RED)
        } catch (e: Exception) {
            Messenger.printMessage(e.toString())
            Messenger.printMessage(Messenger.oops())
        }

    } while (
        commandData != null
        && !(
                commandData.name == "exit"
                        && commandData.args.primitiveTypeArguments == null
                        && commandData.args.organization == null
                )
    )
}