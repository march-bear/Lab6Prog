import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import network.WorkerInterface
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

fun main(args: Array<String>) {
    val app = startKoin {
        modules(clientCommandManagerModule, channelClientWorkerManager)
    }

    val reader = Reader()
    val worker: WorkerInterface
    when (args.size) {
        0 -> {
            Messenger.printMessage("Введите данные сервера")
            Messenger.inputPrompt("Адрес (если localhost, оставьте поле пустым)")
            val host = reader.readStringOrNull()
            Messenger.inputPrompt("Порт")
            var port: Int
            while (true) {
                try {
                    port = reader.readString().toInt()
                    if (port < 1 || port > 65535) throw NumberFormatException()
                    break
                } catch (ex: NumberFormatException) {
                    Messenger.printMessage("Введите целое число от 1 до 65535: ", TextColor.RED, false)
                }
            }

            worker = if (host == null)
                app.koin.get(named("localhost")) { parametersOf(port) }
            else
                app.koin.get { parametersOf(port, host) }
        }
        1 -> {
            val address = args[0].split(":")
            if (address.size != 2) {
                Messenger.printMessage("Нужно указать адрес сервера и порт в формате hostname:port", TextColor.RED)
                return
            }
            worker = app.koin.get { parametersOf(address[1].toInt(), address[0]) }
        }
        2 -> {
            val host = args[0]
            val port: Int
            try {
                port = args[1].toInt()
            } catch (ex: NumberFormatException) {
                Messenger.printMessage("Для определения порта нужно ввести целое число от 0 до 65535", TextColor.RED)
                return
            }
            worker = app.koin.get { parametersOf(port, host) }
        }
        else -> {
            Messenger.printMessage(
                "Подключиться к серверу можно:\n" +
                        "1) указав в качестве аргументов при запуске клиента адрес и порт\n" +
                        "2) указав в качестве аргумента адрес и порт в формате hostname:port\n" +
                        "3) ввести после запуска клиента, не передавая никакие аргумента", TextColor.YELLOW
            )
            return
        }
    }

    worker.start()
}