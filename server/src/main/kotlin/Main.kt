import iostreamers.Messenger
import iostreamers.Reader
import iostreamers.TextColor
import network.WorkerInterface
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
        )
    }

    val reader = Reader()
    val worker: WorkerInterface
    when (args.size) {
        0 -> {
            Messenger.printMessage("Введите порт и название файла")
            Messenger.inputPrompt("Название файла")
            val fileName = reader.readStringOrNull()
            Messenger.inputPrompt("Порт")
            val port = reader.readString().toInt()
            worker = app.koin.get { parametersOf(port, fileName) }
        }
        1 -> {
            val port = args[0].toInt()
            worker = app.koin.get { parametersOf(port, null) }
        }
        2 -> {
            val port = args[0].toInt()
            val fileName = args[1].toInt()
            worker = app.koin.get { parametersOf(port, fileName) }
        }
        else -> {
            Messenger.printMessage("??????????????????????????")
            return
        }
    }

    worker.start()
}