package clientworker

import CommandManager
import OrganizationFactory
import command.Command
import command.CommandData
import exceptions.InvalidArgumentsForCommandException
import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.WorkerInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import requests.Request
import requests.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SelectionKey.*
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.NoSuchElementException
import kotlin.system.exitProcess

class ChannelClientWorker (
    serverPort: Int,
    serverHost: String = "localhost",
) : WorkerInterface, KoinComponent {
    private val remote = InetSocketAddress(serverHost, serverPort)
    private var sock = SocketChannel.open()
    private var selector = Selector.open()
    private val queue: BlockingQueue<Request> = ArrayBlockingQueue(1)
    private val commandManager: CommandManager = get()
    private val buffer: ByteBuffer = ByteBuffer.allocate(65536)

    override fun start() {
        if (!connect()) {
            Messenger.printMessage("Сервер недоступен", TextColor.RED)
            return
        }

        Thread {
            activateInteractiveMode()
            exitProcess(0)
        }.start()

        while (true) {
            selector.select()

            val keys = selector.selectedKeys().iterator()

            for (key in keys) {
                if (key.isValid && key.isWritable) {
                    send(queue.take())
                    sock.register(selector, OP_READ)
                }
                if (key.isValid && key.isReadable) {
                    val response = receive()
                    if (response != null) {
                        Messenger.printMessage("\n${response.message}")
                        Messenger.inputPrompt(">>>", delimiter = " ")
                    }

                    sock.register(selector, OP_WRITE)
                }
                if (key.isValid) keys.remove() else break
            }
        }
    }

    private fun send(req: Request) {
        while (true) {
            try {
                val jsonReq = Json.encodeToString(req)
                buffer.clear()
                buffer.put(jsonReq.toByteArray())
                buffer.flip()

                sock.write(buffer)
                break
            } catch (ex: IOException) {
                Messenger.printMessage("\nВо время отправки запроса связь с сервером была потеряна!", TextColor.RED)
                Messenger.printMessage("Попытка переподключения...")
                if (!reconnect()) {
                    Messenger.printMessage("Не удалось переподключиться к серверу. Выход из программы...", TextColor.RED)
                    exitProcess(6)
                }
                Messenger.printMessage("Переподключение произошло успешно!")
            }
        }
    }

    private fun receive(): Response? {
        while (true) {
            try {
                buffer.clear()
                sock.read(buffer)
                buffer.flip()
                val arr = ByteArray(buffer.limit())
                buffer.get(arr)

                val jsonResponse = String(arr).trim(Char(0))

                return try {
                    Json.decodeFromString<Response>(jsonResponse)
                } catch (ex: SerializationException) { null } catch (ex: IllegalArgumentException) { null }
            } catch (_: IOException) {
                println("РЕКОНЕКТ ВО ВРЕМЯ ПОЛУЧЕНИЯ")
                if (!reconnect()) { exitProcess(6) }
            }
        }
    }

    private fun activateInteractiveMode() {
        val reader = Reader(Scanner(System.`in`))
        var commandData: CommandData?
        var command: Command?

        Messenger.interactiveModeMessage()
        Messenger.printMessage(
            "\nДобро пожаловать в интерактивный режим! " +
                    "Для просмотра доступных команд введите `help`"
        )

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
                            queue.put(request)
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

    private fun connect(): Boolean {
        sock = SocketChannel.open()
        selector = Selector.open()

        sock.configureBlocking(false)
        sock.register(selector, OP_CONNECT)

        sock.connect(remote)

        selector.select()

        val keys: MutableIterator<SelectionKey> = selector.selectedKeys().iterator()
        while (keys.hasNext()) {
            val key = keys.next()
            keys.remove()

            if (key.isConnectable) {
                try {
                    sock.finishConnect()
                    sock.register(selector, OP_WRITE)
                    break
                } catch (_: SocketException) { }
            }
        }

        if (!sock.isConnected) {
            finish()
            return false
        }

        return true
    }

    private fun reconnect(): Boolean {
        finish()
        return connect()
    }

    private fun finish() {
        if (selector.isOpen)
            selector.close()

        if (sock.isOpen)
            sock.close()
    }
}