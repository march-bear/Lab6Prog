package requests

import CollectionController
import CollectionType
import Organization
import collection.*
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable

@Serializable
class ChangeCollectionTypeRequest(private val collectionType: CollectionType) : Request {
    private var oldType: CollectionType? = null
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        val wrapper: CollectionWrapperInterface<Organization> = getWrapperByType(oldType ?: collectionType)
        oldType = collection.getCollectionType()
        collection.replaceCollectionWrapper(wrapper)

        return Response(
            true,
            Messenger.message(
                "Тип коллекции изменен с $oldType на ${collection.getCollectionType()}",
                TextColor.BLUE,
            ),
        )
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        if (oldType == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        val wrapper = getWrapperByType(oldType!!)
        collection.replaceCollectionWrapper(wrapper)

        return Messenger.message("Запрос на смену типа коллекции отменен", TextColor.BLUE)
    }

    private fun getWrapperByType(type: CollectionType): CollectionWrapperInterface<Organization> {
        return when (type) {
            CollectionType.SET -> LinkedHashSetWrapper()
            CollectionType.QUEUE -> ConcurrentLinkedQueueWrapper()
            CollectionType.LIST -> LinkedListWrapper()
        }
    }
}