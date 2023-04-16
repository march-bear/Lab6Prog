package network.receiver

fun interface ReceiverInterface<T> {
    fun receive(): T
}