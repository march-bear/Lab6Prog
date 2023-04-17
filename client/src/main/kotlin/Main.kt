import iostreamers.Messenger
import iostreamers.Reader
import network.WorkerInterface
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf

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
            val port = reader.readString().toInt()
            worker = if (host == null)
                app.koin.get { parametersOf(port) }
            else
                app.koin.get { parametersOf(port, host) }
        }
        1 -> {
            val address = args[0].split(":")
            if (address.size != 2) {
                Messenger.printMessage("??????????????????")
                return
            }
            worker = app.koin.get { parametersOf(address[1].toInt(), address[0]) }
        }
        2 -> {
            val host = args[0]
            val port = args[1].toInt()
            worker = app.koin.get { parametersOf(port, host) }
        }
        else -> {
            Messenger.printMessage("??????????????????????????")
            return
        }
    }

    worker.start()
}