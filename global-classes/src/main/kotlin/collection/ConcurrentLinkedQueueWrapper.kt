package collection

import java.util.concurrent.ConcurrentLinkedQueue

class ConcurrentLinkedQueueWrapper<E>(
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