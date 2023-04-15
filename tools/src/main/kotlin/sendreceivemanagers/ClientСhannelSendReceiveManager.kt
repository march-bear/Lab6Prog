package sendreceivemanagers


import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import requests.Request
import requests.Response
import java.io.IOException
import java.net.ConnectException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class ClientChannelSendReceiveManager(
    port: Int,
    hostname: String = "localhost",
): SendReceiveManagerInterface<Request, Response> {
    private val client: SocketChannel
    private var buffer = ByteBuffer.allocate(1024)
    private val selector = Selector.open()

    init {
        val remote = InetSocketAddress(hostname, port)
        client = SocketChannel.open()
        client.configureBlocking(false)
        client.register(selector, SelectionKey.OP_CONNECT)

        if (!client.connect(remote)) {
            if (selector.select(20000) == 0)
                try {
                    client.finishConnect()
                } catch (e: IOException) {
                    throw ConnectException("Превышено время ожидания подключения")
                }

            val keys = selector.selectedKeys()
            for (key in keys) {
                if (key.isConnectable) {
                    client.finishConnect()
                    key.interestOps(key.interestOps() and SelectionKey.OP_CONNECT.inv())
                }
            }
        }
    }

    override fun send(message: Request) {

        val jsonOfRequest = Json.encodeToString(message)
        val byteArrOfRequest = jsonOfRequest.toByteArray()

        buffer = ByteBuffer.wrap(byteArrOfRequest)
        client.write(buffer)

        buffer.flip()
    }


    override fun receive(): Response {

        return Response(true, "", false)
    }
}