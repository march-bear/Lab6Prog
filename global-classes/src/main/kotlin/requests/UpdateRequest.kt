package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor

/**
 * Запрос на обновление значения элемента по его id
 */
class UpdateRequest(
    private val id: Long,
    element: Organization,
) : Request {
    private var oldValue: Organization? = null
    private var collection: CollectionWrapper<Organization>? = null
    private val newValue: Organization

    init {
        element.id = id
        newValue = element.clone()
    }
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        oldValue = collection.find { it.id == id}
        if (oldValue != null) {
            collection.replace(oldValue!!, newValue.clone())
            this.collection = collection
            return Response(
                true,
                Messenger.message("Значение элемента с id $id обновлено", TextColor.BLUE)
            )
        }

        return Response(false, Messenger.message("Элемент с id=$id не найден", TextColor.RED))
    }

    override fun cancel(): String {
        if (oldValue == null || collection == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        if (collection!!.find { it == newValue} != null) {
            collection!!.replace(newValue, oldValue!!)
            oldValue = null
            collection = null

            return Messenger.message("Запрос на обновление значения элемента отменен", TextColor.BLUE)
        }

        throw CancellationException("Отмена запроса невозможна, так как коллекция уже была модифицирована")
    }
}