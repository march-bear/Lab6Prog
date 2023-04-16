package serverworker

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import network.receiver.ReceiverInterface
import requests.Request
import java.net.Socket

class StreamServerReceiver(private val socket: Socket) : ReceiverInterface<Request?> {
    override fun receive(): Request? {
        val arr = ByteArray(65536)
        socket.getInputStream().read(arr)

        return try {
            val jsonMessage = String(arr).trim(Char(0))
            Json.decodeFromString(jsonMessage)
        } catch (ex: SerializationException) {
            null
        } catch (ex: IllegalArgumentException) {
            null
        }
    }
}