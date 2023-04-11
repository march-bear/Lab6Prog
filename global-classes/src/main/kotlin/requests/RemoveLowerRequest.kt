package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import collection.LinkedListSerializer
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Запрос на удаление из коллекции всех элементов, меньших, чем данный
 */

@Serializable
class RemoveLowerRequest(private val element: Organization) : Request {
    @Serializable(with = LinkedListSerializer::class)
    private val removedElements: LinkedList<Organization> = LinkedList()
    private var collection: CollectionWrapper<Organization>? = null

    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        var output = ""

        for (elem in collection.clone()) {
            if (elem < element) {
                collection.remove(elem)
                output += Messenger.message(
                    "Удален элемент с id ${elem.id}\n",
                    TextColor.BLUE
                )
            }
        }
        this.collection = collection
        if (output != "")
            return Response(true, output)

        return Response(
            true,
            Messenger.message("В коллекции нет элементов, меньших, чем введенный", TextColor.BLUE)
        )
    }

    override fun cancel(): String {
        if (collection == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        for (removedElement in removedElements) {
            if (!CollectionController.checkUniquenessFullName(removedElement.fullName, collection!!) ||
                !CollectionController.checkUniquenessId(removedElement.id, collection!!)
            )
                throw CancellationException(
                    "Отмена запроса невозможна, так как в коллекции уже есть элемент с таким же " +
                            "id или полным именем"
                )
        }

        for (removedElement in removedElements)
            collection!!.add(removedElement)

        removedElements.clear()
        collection = null

        return "Запрос на удаление элементов отменен"
    }
}