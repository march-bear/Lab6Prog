package serverworker

import CollectionController
import CommandManager
import OrganizationFactory
import command.Command
import command.CommandData
import exceptions.InvalidArgumentsForCommandException
import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import network.WorkerInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import requests.Response
import java.io.File
import java.net.ServerSocket
import java.net.SocketException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.system.exitProcess

class StreamServerWorker(
    port: Int,
    fileName: String?
) : WorkerInterface, KoinComponent {
    private val serv: ServerSocket = ServerSocket(port)
    private val commandManager: CommandManager = get()
    private val cController: CollectionController = get { parametersOf(if (fileName == null) null else File(fileName)) }

    override fun start() {
        Thread {
            enableGodMode()
        }.start()

        while (true) {
            val sock = serv.accept()

            val receiver = StreamServerReceiver(sock)
            val sender = StreamServerSender(sock)
            try {
                while (true) {
                    val request = receiver.receive()
                    if (request == null) {
                        sender.send(Response(false, "Запрос некорректен"))
                    } else {
                        sender.send(cController.process(request))
                    }
                }
            } catch (_: SocketException) { sock.close() }
        }
    }

    private fun enableGodMode() {
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
                        if (request != null) {
                            val (processing, output, _) = cController.process(request)
                            Messenger.printMessage(output, if (processing) TextColor.BLUE else TextColor.RED)
                        }
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

        exitProcess(0)
    }
}