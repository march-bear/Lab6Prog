package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import collection.LinkedListSerializer
import command.Command
import command.CommandArgument
import exceptions.CancellationException
import exceptions.CommandIsNotCompletedException
import exceptions.InvalidArgumentsForCommandException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Запрос на исполнение команд
 * @param commands список команд на исполнение,
 * хранящий пары "Объект команды": "Аргументы команды"
 */

@Serializable
class ExecuteCommandsRequest(
    @Serializable(with = LinkedListSerializer::class)
    private val commands: LinkedList<Pair<Command, CommandArgument>>,
) : Request {
    private val requests: Stack<Request> = Stack()
    private var collection: CollectionWrapper<Organization>? = null
    private var cController: CollectionController? = null
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        this.collection = collection
        this.cController = cController
        var output = ""
        try {
            for (i in commands.indices) {
                val (command, args) = commands[i]
                val (commandCompleted, request, message) = command.execute(args)
                if (commandCompleted) {
                    if (message != null) output += Messenger.message(message) + "\n"
                    if (request != null) {
                        val (requestCompleted, message) = request.process(collection, cController)
                        output += message + "\n"
                        if (requestCompleted) {
                            requests.add(request)
                        } else {
                            throw CommandIsNotCompletedException(message)
                        }

                    }
                } else
                    throw CommandIsNotCompletedException("Команда не была выполнена. Сообщение о выполнении:\n" +
                            "$message")
            }
        } catch (ex: CommandIsNotCompletedException) {
            cancel()
            return Response(false, Messenger.message(
                "Ошибка во время исполнения скрипта. Сообщение ошибки:\n$ex",
                TextColor.RED))
        } catch (ex: InvalidArgumentsForCommandException) {
            cancel()
            return Response(false, Messenger.message(
                "Ошибка во время исполнения скрипта. Сообщение ошибки:\n$ex",
                TextColor.RED))
        }
        return Response(true, Messenger.message("Скрипт выполнен. Вывод:\n$output", TextColor.BLUE))
    }

    override fun cancel(): String {
        if (collection == null || cController == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        val canceledRequests: Stack<Request> = Stack()
        try {
            while (requests.isNotEmpty()) {
                canceledRequests.add(requests.pop())
                canceledRequests.peek().cancel()
            }
        } catch (ex: CancellationException) {
            canceledRequests.pop()
            while(canceledRequests.isNotEmpty())
                canceledRequests.pop().process(collection!!, cController!!)
            throw CancellationException("Отмена запроса невозможна. Коллекция уже была модифицирована")
        }

        collection = null
        cController = null

        requests.clear()
        return "Запрос на исполнение скрипта отменен"
    }
}