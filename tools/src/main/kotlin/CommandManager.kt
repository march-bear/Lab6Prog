import command.Command
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class CommandManager(
    private val module: Module,
) {
    private val koinApp = koinApplication {
        modules(module, module { single { this@CommandManager } })
    }

    fun getCommand(name: String): Command? = try {
            koinApp.koin.get(named(name))
        } catch (ex: NoBeanDefFoundException) {
            null
        }
}