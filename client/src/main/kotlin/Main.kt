import iostreamers.Messenger
import iostreamers.Reader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.channels.SocketChannel

fun main(args: Array<String>) {
    val r = Reader()
    Messenger.printMessage("Привет, красавчик, введи данные сервака:")
    Messenger.inputPrompt("IP или доменное имя")
    val host = InetAddress.getByName(r.readStringOrNull())
    Messenger.inputPrompt("Порт")
    val port = r.readString().toInt()
}