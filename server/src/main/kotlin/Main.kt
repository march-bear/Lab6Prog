import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.error.InstanceCreationException
import org.koin.core.parameter.parametersOf
import java.io.FileNotFoundException

fun main(args: Array<String>) {
    val app = startKoin {
        modules(
            serverWorkerModule,
            serverCommandManagerModule,
            basicCollectionControllerModule,
            linkedListWrapperModule,
            serverStreamSendReceiveManagerModule,
        )
    }

    val r = Reader()

    val serverWorker: ServerWorker

    while (true) {
        Messenger.printMessage("Создание сервера...")
        Messenger.printMessage("Введите путь к файлу с коллекцией и номер порта")
        Messenger.inputPrompt("Путь к файлу")
        val file = r.readStringOrNull()
        Messenger.inputPrompt("Порт (для автоматического определения оставьте пустым)")
        val port = r.readStringOrNull()
        if (port == null) {
            Messenger.printMessage("Сорян, такая функция пока не работает :)")
            return
        }
        serverWorker = app.koin.get { parametersOf(port.toInt(), file) }
        try {
            //serverWorker = app.koin.get { parametersOf(port, file) }
            break
        } catch (ex: InstanceCreationException) {
            if (ex.cause != null && ex.cause!!::class == FileNotFoundException::class) {
                Messenger.printMessage(
                    "Ошибка во время открытия файла с коллекцией: ${ex.cause!!.message}", TextColor.RED
                )
                return
            }

            Messenger.printMessage(
                "ServerWorker не может быть инициализирован. (не) Бейте разработчика",
                TextColor.RED
            )
            Messenger.printMessage(ex.toString())
            return
        } catch (ex: Exception) { println(ex); println(Messenger.oops()); return }
    }

    Messenger.printMessage("Сервер инициализирован")
    Messenger.printMessage("ЗАПУСК СЕРВАКА")
    serverWorker.start()
}