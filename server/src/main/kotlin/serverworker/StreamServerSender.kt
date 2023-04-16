package serverworker

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.sender.SenderInterface
import requests.Response
import java.net.Socket

class StreamServerSender(private val sock: Socket) : SenderInterface<Response> {
    override fun send(message: Response) {
        val jsonMessage = Json.encodeToString(message)
        val arr = (jsonMessage.toByteArray())
        sock.getOutputStream().write(arr)
    }
}