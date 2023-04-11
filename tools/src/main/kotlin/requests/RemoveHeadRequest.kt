package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable

/**
 * Запрос на удаление первого элемента в коллекции
 */

@Serializable
class RemoveHeadRequest : Request {
    private var removedElement: Organization? = null

    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        if (collection.isEmpty())
            return Response(
                false,
                Messenger.message("Элемент не может быть удален - коллекция пуста", TextColor.RED)
            )

        removedElement = collection.remove()
        return Response(true, "-------------------------\n" +
                removedElement.toString() +
                "\n-------------------------" +
                Messenger.message("\nЭлемент удален", TextColor.BLUE))
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        if (removedElement == null)
            return "Запрос на удаление элемента отменен"

        if (!CollectionController.checkUniquenessFullName(removedElement!!.fullName, collection) ||
            !CollectionController.checkUniquenessId(removedElement!!.id, collection))
            throw CancellationException("Отмена запроса невозможна, так как в коллекции уже есть элемент с таким же " +
                    "id или полным именем")

        collection.add(removedElement!!)
        removedElement = null

        return "Запрос на удаление элемента отменен"
    }
}