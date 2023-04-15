package sendreceivemanagers

interface SendReceiveManagerInterface<T, R> {
    fun send(message: T)
    fun receive(): R
}