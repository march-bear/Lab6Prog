package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable

/**
 * Запрос на добавление нового элемента в коллекцию, если он является максимальным
 */

@Serializable
class AddIfMaxRequest(
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

        if (collection.isEmpty() || newElement!! > collection.max()) {
            collection.add(newElement!!)
            this.collection = collection
            return Response(
                true,
                Messenger.message("Элемент добавлен в коллекцию", TextColor.BLUE)
            )
        }
        return Response(
            true,
            Messenger.message("Элемент не является максимальным", TextColor.BLUE),
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