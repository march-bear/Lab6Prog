package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable

/**
 * Запрос на обновление значения элемента по его id
 */

@Serializable
class UpdateRequest(
    private val id: Long,
    private val element: Organization,
) : Request {
    private var oldValue: Organization? = null
    private val newValue: Organization

    init {
        element.id = id
        newValue = element.clone()
    }
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        oldValue = collection.find { it.id == id}
        if (oldValue != null) {
            collection.replace(oldValue!!, newValue.clone())
            return Response(
                true,
                Messenger.message("Значение элемента с id $id обновлено", TextColor.BLUE)
            )
        }

        return Response(false, Messenger.message("Элемент с id=$id не найден", TextColor.RED))
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        if (oldValue == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        if (collection.find { it == newValue} != null) {
            collection.replace(newValue, oldValue!!)
            oldValue = null

            return Messenger.message("Запрос на обновление значения элемента отменен", TextColor.BLUE)
        }

        throw CancellationException("Отмена запроса невозможна, так как коллекция уже была модифицирована")
    }
}