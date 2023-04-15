import collection.CollectionWrapper
import collection.CollectionWrapperInterface
import collection.LinkedListWrapper
import command.*
import command.implementations.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sendreceivemanagers.ServerStreamSendReceiveManager
import java.io.File
import kotlin.collections.HashMap

val commandQualifiers = listOf(
    "info", "show", "add", "update",
    "remove_by_id", "clear", "save",
    "execute_script", "exit", "remove_head",
    "add_if_max", "remove_lower",
    "sum_of_employees_count", "oops",
    "group_counting_by_employees_count",
    "print_unique_postal_address",
    "show_field_requirements",
    "change_collection_type", "rollback"
)

val serverCommandModule = module {
    single<Command>(named("help")) {
        val commandsMap = HashMap<String, String>()
        commandsMap["help"] = HelpCommand(mapOf()).info
        for (qualifier in commandQualifiers) {
            commandsMap[qualifier] = this.getKoin().get<Command>(named(qualifier)).info
        }
        HelpCommand(commandsMap)
    }

    single<Command>(named("info")) { InfoCommand() }
    single<Command>(named("show")) { ShowCommand() }
    single<Command>(named("add")) { AddCommand() }
    single<Command>(named("update")) { UpdateCommand() }
    single<Command>(named("remove_by_id")) { RemoveByIdCommand() }
    single<Command>(named("clear")) { ClearCommand() }
    single<Command>(named("save")) { SaveCommand() }

    single<Command>(named("execute_script")) {
        ExecuteScriptCommand(get())
    }

    single<Command>(named("exit")) { ExitCommand() }
    single<Command>(named("remove_head")) { RemoveHeadCommand() }
    single<Command>(named("add_if_max")) { AddIfMaxCommand() }
    single<Command>(named("remove_lower")) { RemoveLowerCommand() }
    single<Command>(named("sum_of_employees_count")) { SumOfEmployeesCountCommand() }
    single<Command>(named("group_counting_by_employees_count")) { GroupCountingByEmployeesCountCommand() }
    single<Command>(named("print_unique_postal_address")) { PrintUniquePostalAddressCommand() }

    single<Command>(named("oops")) {
        object : Command {
            override val info: String
            get() = "взломать систему"

            override fun execute(args: CommandArgument): CommandResult {
                return CommandResult(
                    false,
                    message = "Руки поотрывать тому, кто на сервере такие команды использует"
                )
            }
        }
    }

    single<Command>(named("show_field_requirements")) { ShowFieldRequirementsCommand() }
    single<Command>(named("change_collection_type")) { ChangeCollectionTypeCommand() }
    single<Command>(named("rollback")) { RollbackCommand() }
}

val serverCommandManagerModule = module {
    single {
        serverCommandModule
    }

    single {
        CommandManager(get())
    }
}

val linkedListWrapperModule = module {
    factory<CollectionWrapperInterface<Organization>> {
        LinkedListWrapper()
    }
}

val basicCollectionControllerModule = module {
    single { (file: File) -> CollectionController(file) }

    single<CollectionWrapper<Organization>> { CollectionWrapper(get()) }
}

val serverStreamSendReceiveManagerModule = module {
    factory {(port: Int) ->
        ServerStreamSendReceiveManager(port)
    }
}

val serverWorkerModule = module {
    single { (port: Int, fileName: String?) ->
        ServerWorker(port, if (fileName != null) File(fileName) else null)
    }
}