package serverworker

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.sender.SenderInterface
import org.slf4j.Logger
import requests.Response
import java.net.Socket

class StreamServerSender(
    private val sock: Socket,
    private val log: Logger,
) : SenderInterface<Response> {
    override fun send(message: Response) {
        val jsonMessage = Json.encodeToString(message)
        val arr = (jsonMessage.toByteArray())
        sock.getOutputStream().write(arr)
        log.info("Отправлено сообщение для $sock")
    }
}