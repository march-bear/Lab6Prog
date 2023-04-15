import command.Command
import command.CommandData
import exceptions.InvalidArgumentsForCommandException
import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import requests.Request
import requests.Response
import sendreceivemanagers.SendReceiveManagerInterface
import java.io.File
import java.net.Socket
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.NoSuchElementException

class ServerWorker(
    port: Int,
    file: File?,
) : KoinComponent {
    private val controller: CollectionController = get { parametersOf(file) }
    private val sendReceiveManager: SendReceiveManagerInterface<Response, Request?> by inject { parametersOf(port) }
    private val commandManager: CommandManager by inject()
    private val requests: Queue<Pair<Socket?, Request>> = ConcurrentLinkedQueue()

    fun start() {
        val interactiveMode = Thread { enableInteractiveMode() }
        val requestsProcessing = Thread {
            while(true)
                if (requests.isNotEmpty()) {
                    val (socket, request) = requests.poll()
                    val response = controller.execute(request)
                    if (socket == null) {
                        Messenger.printMessage(
                            "\n${response.message}",
                            if (response.requestCompleted) TextColor.BLUE else TextColor.RED,
                        )
                        Messenger.inputPrompt(">>>")
                    }
                }
        }

        interactiveMode.start()

        requestsProcessing.isDaemon = true
        requestsProcessing.start()
    }

    private fun enableInteractiveMode() {
        Messenger.godModeActivatedMessage()
        Messenger.printMessage(
            "\nДобро пожаловать в интерактивный режим сервера! " +
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
                        if (request != null)
                            requests.add(Pair(null, request))
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

        Messenger.printMessage("Выход из интерактивного режима...")
    }
}