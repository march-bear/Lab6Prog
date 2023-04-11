package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable

/**
 * Запрос на очистку коллекции
 */

@Serializable
class ClearRequest : Request {
    private var oldCollection: CollectionWrapper<Organization>? = null
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        oldCollection = collection.clone() as CollectionWrapper<Organization>
        collection.clear()

        return Response(true, Messenger.message("Коллекция очищена", TextColor.BLUE))
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        if (oldCollection == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")
        if (collection.isEmpty())
            throw CancellationException("Отмена запроса невозможна - коллекция уже была модифицирована")
        collection.addAll(oldCollection!!)

        oldCollection = null

        return "Запрос на очистку коллекции отменен"
    }
}