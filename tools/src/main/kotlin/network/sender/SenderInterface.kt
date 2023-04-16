package network.sender

fun interface SenderInterface<T> {
    fun send(message: T)
}