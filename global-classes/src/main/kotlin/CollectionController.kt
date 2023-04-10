import collection.CollectionWrapper
import commandcallgraph.RequestGraph
import iostreamers.Messenger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import requests.Request
import requests.Response
import java.io.File

class CollectionController(
    dataFileName: File? = null,
) : KoinComponent {
    companion object {
        fun checkUniquenessFullName(fullName: String?, collection: CollectionWrapper<Organization>): Boolean {
            if (fullName == null)
                return true

            for (elem in collection)
                if (elem.fullName != null && elem.fullName == fullName)
                    return false
            return true
        }

        fun checkUniquenessId(id: Long, collection: CollectionWrapper<Organization>): Boolean {
            if (!Organization.idIsValid(id))
                return false

            for (elem in collection)
                if (elem.id == id)
                    return false
            return true
        }
    }

    private val collection: CollectionWrapper<Organization> by inject()
    val dataFileManager: DataFileManager = DataFileManager(collection, dataFileName)
    val idManager: IdManager
    val requestGraph: RequestGraph = RequestGraph(collection, this)

    fun execute(request: Request) : Response {
        val response = request.process(collection, this)
        if (response.archivable) {
            requestGraph.addLeaf(request, request::class.simpleName?.uppercase() ?: "REQUEST")
        }

        return response
    }

    init {
        Messenger.printMessage("Начало загрузки коллекции. Это может занять некоторое время...")

        val output = dataFileManager.loadData()

        Messenger.printMessage("Загрузка коллекции завершена. Отчет о выполнении загрузки:")
        Messenger.printMessage("---------------------------------------------------------------------")
        Messenger.printMessage(output)
        Messenger.printMessage("---------------------------------------------------------------------\n")

        idManager = IdManager(collection)
    }
}