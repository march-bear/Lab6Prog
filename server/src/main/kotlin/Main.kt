import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import network.WorkerInterface
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.parameter.parametersOf

fun main(args: Array<String>) {
    val app = startKoin {
        modules(
            serverWorkerModule,
            serverCommandManagerModule,
            basicCollectionControllerModule,
            linkedListWrapperModule,
        )
    }

    val reader = Reader()
    var worker: WorkerInterface
    when (args.size) {
        0 -> {
            Messenger.printMessage("Введите порт и название файла")
            Messenger.inputPrompt("Название файла")
            val fileName = reader.readStringOrNull()
            Messenger.inputPrompt("Порт")
            var port: Int
            while (true) {
                try {
                    port = reader.readString().toInt()
                    if (port < 0 || port > 65535) throw NumberFormatException()
                    worker = app.koin.get { parametersOf(port, fileName) }
                    break
                } catch (ex: NumberFormatException) {
                    Messenger.printMessage("Введите целое число от 0 до 65535: ", TextColor.RED, false)
                }
            }
        }
        1 -> {
            val port: Int
            try {
                port = args[0].toInt()
                if (port < 0 || port > 65535) throw NumberFormatException()
                worker = app.koin.get { parametersOf(port, null) }
            } catch (ex: NumberFormatException) {
                Messenger.printMessage("Для определения порта нужно ввести целое число от 0 до 65535: ", TextColor.RED)
                return
            }
        }
        2 -> {
            val port: Int
            try {
                port = args[0].toInt()
                if (port < 0 || port > 65535) throw NumberFormatException()
                val fileName = args[1]
                worker = app.koin.get { parametersOf(port, fileName) }
            } catch (ex: NumberFormatException) {
                Messenger.printMessage("Для определения порта нужно ввести целое число от 0 до 65535: ", TextColor.RED)
                return
            }
        }
        else -> {
            Messenger.printMessage(
                "Создать сервер можно:\n" +
                        "1) указав в качестве аргументов порт и путь к файлу с коллекцией\n" +
                        "2) указав к качестве аргумента только порт (путь будет взят по умолчанию)\n" +
                        "3) введя название файла и порт после запуска приложения, не указывая аргументы", TextColor.YELLOW)
            return
        }
    }

    worker.start()
}