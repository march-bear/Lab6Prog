package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import iostreamers.Messenger
import iostreamers.TextColor
import exceptions.CancellationException
import kotlinx.serialization.Serializable

/**
 * Запрос на удаление элемента из коллекции по его id
 */

@Serializable
class RemoveByIdRequest(private val id: Long) : Request {
    private var removedElement: Organization? = null

    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        removedElement = collection.find { it.id == id }

        if (removedElement != null) {
            collection.remove(removedElement!!)
            return Response(true, Messenger.message("Элемент удален", TextColor.BLUE))
        }

        return Response(false, Messenger.message("Элемент с id $id не найден", TextColor.RED))
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        if (removedElement == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        if (!CollectionController.checkUniquenessFullName(removedElement!!.fullName, collection) ||
            !CollectionController.checkUniquenessId(removedElement!!.id, collection))
            throw CancellationException("Отмена запроса невозможна, так как в коллекции уже есть элемент с таким же " +
                    "id или полным именем")

        collection.add(removedElement!!)
        removedElement = null

        return "Запрос на удаление элемента отменен"
    }
}