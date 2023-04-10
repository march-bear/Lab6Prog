package collection

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.util.LinkedList

class LinkedListWrapper<E>(
    private val linkedList: LinkedList<E> = LinkedList(),
) : CollectionWrapperInterface<E> {
    override val size: Int
        get() = linkedList.size

    override fun add(element: E): Boolean = linkedList.add(element)

    override fun replace(curr: E, new: E) {
        linkedList[linkedList.indexOf(curr)] = new
    }

    override fun replaceBy(new: E, predicate: (E) -> Boolean) {
        linkedList[
                linkedList.indexOf(
                    linkedList.find(predicate)
                )
        ] = new
    }

    override fun isEmpty(): Boolean = linkedList.isEmpty()

    override fun clear() = linkedList.clear()

    override fun remove(): E = linkedList.remove()

    override fun iterator(): Iterator<E> = linkedList.iterator()

    override fun remove(element: E): Boolean = linkedList.remove(element)

    override fun getCollectionName(): String = "LinkedList"

    override fun getCollectionType(): CollectionType = CollectionType.LIST

    override fun clone(): CollectionWrapperInterface<E> {
        val linkedListCopy = LinkedListWrapper<E>()
        linkedListCopy.addAll(linkedList)
        return linkedListCopy
    }
}

class LinkedListSerializer<E>(private val serializer: KSerializer<E>): KSerializer<LinkedList<E>> {
    override fun serialize(enc: Encoder, obj: LinkedList<E>) {
        enc.encodeSerializableValue(ListSerializer(serializer), obj.toList())
    }

    private val listSerializer = ListSerializer(serializer)
    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun deserialize(decoder: Decoder): LinkedList<E> = with(decoder as JsonDecoder) {
        val list = decodeJsonElement().jsonArray.mapNotNull {
            try {

                json.decodeFromJsonElement(serializer, it)
            } catch (e: SerializationException) {
                e.printStackTrace()
                null
            }
        }
        val linkedList = LinkedList<E>()
        linkedList.addAll(list)
        linkedList
    }
}
