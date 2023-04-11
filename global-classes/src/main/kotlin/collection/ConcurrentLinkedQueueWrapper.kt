package collection

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonArray
import java.util.concurrent.ConcurrentLinkedQueue

@Serializable
class ConcurrentLinkedQueueWrapper<E>(
    @Serializable(with = ConcurrentLinkedQueueSerializer::class)
    private val queue: ConcurrentLinkedQueue<E> = ConcurrentLinkedQueue()
): CollectionWrapperInterface<E> {
    override val size: Int
        get() = queue.size

    override fun add(element: E): Boolean = queue.add(element)

    override fun replace(curr: E, new: E) {
        queue.remove(curr) && queue.add(new)
    }

    override fun replaceBy(new: E, predicate: (E) -> Boolean) {
        queue.remove(queue.find(predicate)) && queue.add(new)
    }

    override fun isEmpty(): Boolean = queue.isEmpty()

    override fun clear() = queue.clear()

    override fun remove(): E = queue.remove()

    override fun remove(element: E): Boolean = queue.remove(element)

    override fun iterator(): Iterator<E> = queue.iterator()

    override fun getCollectionName(): String = "ConcurrentLinkedQueue"

    override fun clone(): CollectionWrapperInterface<E> {
        val queueCopy = ConcurrentLinkedQueueWrapper<E>()
        queueCopy.addAll(queue)
        return queueCopy
    }

    override fun getCollectionType(): CollectionType = CollectionType.QUEUE
}

class ConcurrentLinkedQueueSerializer<E>(private val elementSerializer: KSerializer<E>) : KSerializer<ConcurrentLinkedQueue<E>> {
    private val listSerializer = ListSerializer(elementSerializer)
    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: ConcurrentLinkedQueue<E>) {
        listSerializer.serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): ConcurrentLinkedQueue<E> {
        val list = with(decoder as JsonDecoder) {
            decodeJsonElement().jsonArray.mapNotNull {
                try {
                    json.decodeFromJsonElement(elementSerializer, it)
                } catch (e: SerializationException) {
                    e.printStackTrace()
                    null
                }
            }
        }

        return ConcurrentLinkedQueue(list)
    }
}