package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor

/**
 * Запрос на добавление нового элемента в коллекцию
 */
class AddRequest(
    private val element: Organization,
) : Request {
    private var newElement: Organization? = null
    private var collection: CollectionWrapper<Organization>? = null
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        if (newElement == null || !CollectionController.checkUniquenessId(newElement!!.id, collection)) {
            element.id = cController.idManager.generateId()
                ?: return Response(false, Messenger.message("Коллекция переполнена", TextColor.RED))
            newElement = element.clone()
        }

        collection.add(newElement!!)
        this.collection = collection

        return Response(
            true,
            Messenger.message("Элемент добавлен в коллекцию", TextColor.BLUE),
        )
    }

    override fun cancel(): String {
        if (newElement == null || collection == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        val res = collection!!.remove(newElement!!)
        collection = null

        if (res)
            return "Запрос на добавление элемента отменен"
        else
            throw CancellationException("Отмена запроса невозможна - добавленный элемент уже был подвергнут изменениям")
    }
}