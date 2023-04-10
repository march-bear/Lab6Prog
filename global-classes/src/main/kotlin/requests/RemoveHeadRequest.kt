package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor

/**
 * Запрос на удаление первого элемента в коллекции
 */
class RemoveHeadRequest : Request {
    private var removedElement: Organization? = null
    private var collection: CollectionWrapper<Organization>? = null

    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        this.collection = collection
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

    override fun cancel(): String {
        if (collection == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        if (removedElement == null)
            return "Запрос на удаление элемента отменен"

        if (!CollectionController.checkUniquenessFullName(removedElement!!.fullName, collection!!) ||
            !CollectionController.checkUniquenessId(removedElement!!.id, collection!!))
            throw CancellationException("Отмена запроса невозможна, так как в коллекции уже есть элемент с таким же " +
                    "id или полным именем")

        collection!!.add(removedElement!!)
        removedElement = null
        collection = null
        return "Запрос на удаление элемента отменен"
    }
}