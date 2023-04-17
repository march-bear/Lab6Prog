package serverworker

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import network.receiver.ReceiverInterface
import org.slf4j.Logger
import requests.Request
import java.net.Socket

class StreamServerReceiver(
    private val sock: Socket,
    private val log: Logger,
) : ReceiverInterface<Request?> {
    override fun receive(): Request? {
        val arr = ByteArray(65536)
        sock.getInputStream().read(arr)
        log.info("Получено сообщение от $sock")
        val request = try {
            val jsonMessage = String(arr).trim(Char(0))
            Json.decodeFromString<Request>(jsonMessage)
        } catch (ex: SerializationException) {
            null
        } catch (ex: IllegalArgumentException) {
            null
        }

        if (request == null) log.error("Полученное от $sock сообщение не соответствует формату запроса")
        else log.info("Сообщение от $sock декодировано в запрос $request")

        return request
    }
}