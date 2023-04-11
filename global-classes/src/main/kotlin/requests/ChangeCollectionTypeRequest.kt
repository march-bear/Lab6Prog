package requests

import CollectionController
import Organization
import collection.*
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable

@Serializable
class ChangeCollectionTypeRequest(private val collectionType: CollectionType) : Request {
    private var collection: CollectionWrapper<Organization>? = null
    private var oldType: CollectionType? = null
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        val wrapper: CollectionWrapperInterface<Organization> = getWrapperByType(oldType ?: collectionType)
        this.collection = collection
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

    override fun cancel(): String {
        if (oldType == null || collection == null)
            throw CancellationException("Отмена запроса невозможна, так как он ещё не был выполнен или уже был отменен")

        val wrapper = getWrapperByType(oldType!!)
        collection!!.replaceCollectionWrapper(wrapper)
        collection = null

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