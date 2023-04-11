package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import command.Command
import command.CommandArgument
import exceptions.CancellationException
import exceptions.CommandIsNotCompletedException
import exceptions.InvalidArgumentsForCommandException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonArray
import serializers.LinkedListSerializer
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
    @Serializable(with = StackSerializer::class)
    private val requests: Stack<Request> = Stack()

    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
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
            cancel(collection, cController)
            return Response(false, Messenger.message(
                "Ошибка во время исполнения скрипта. Сообщение ошибки:\n$ex",
                TextColor.RED))
        } catch (ex: InvalidArgumentsForCommandException) {
            cancel(collection, cController)
            return Response(false, Messenger.message(
                "Ошибка во время исполнения скрипта. Сообщение ошибки:\n$ex",
                TextColor.RED))
        }
        return Response(true, Messenger.message("Скрипт выполнен. Вывод:\n$output", TextColor.BLUE))
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        val canceledRequests: Stack<Request> = Stack()
        try {
            while (requests.isNotEmpty()) {
                canceledRequests.add(requests.pop())
                canceledRequests.peek().cancel(collection, cController)
            }
        } catch (ex: CancellationException) {
            canceledRequests.pop()
            while(canceledRequests.isNotEmpty())
                canceledRequests.pop().process(collection, cController)
            throw CancellationException("Отмена запроса невозможна. Коллекция уже была модифицирована")
        }

        requests.clear()
        return "Запрос на исполнение скрипта отменен"
    }
}

class StackSerializer<E>(private val elementSerializer: KSerializer<E>) : KSerializer<Stack<E>> {
    private val listSerializer = ListSerializer(elementSerializer)
    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Stack<E>) {
        listSerializer.serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): Stack<E> {
        val list = with(decoder as JsonDecoder) {
            decodeJsonElement().jsonArray.mapNotNull {
                try {
                    json.decodeFromJsonElement(elementSerializer, it)
                } catch (e: SerializationException) {
                    e.printStackTrace()
                    null
                }
            }
        }
        val res = Stack<E>()
        res.addAll(list)
        return res
    }
}