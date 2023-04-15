package sendreceivemanagers

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import requests.Request
import requests.Response
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket

class ServerStreamSendReceiveManager(
    port: Int,
) : SendReceiveManagerInterface<Response, Request?> {
    private val serv = ServerSocket(port)
    private val inputStream: InputStream
    private val outputStream: OutputStream
    private val byteArray = ByteArray(65536)

    init {
        val sock = serv.accept()
        inputStream = sock.getInputStream()
        outputStream = sock.getOutputStream()
    }

    override fun send(message: Response) {
        val jsonResponse = Json.encodeToString(Response)
        byteArray.fill(0)
    }

    override fun receive(): Request? {
        inputStream.read(byteArray)
        val jsonRequest = String(byteArray)
        return try {
            Json.decodeFromString<Request>(jsonRequest)
        } catch (ex: IllegalArgumentException) {
            null
        } catch (ex: SerializationException) {
            null
        }
    }
}