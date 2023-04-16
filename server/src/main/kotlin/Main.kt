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

    val worker = app.koin.get<WorkerInterface> { parametersOf(5555, null) }
    worker.start()
}