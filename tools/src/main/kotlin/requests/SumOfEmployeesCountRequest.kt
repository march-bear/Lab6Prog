package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable
import kotlin.coroutines.cancellation.CancellationException

@Serializable
class SumOfEmployeesCountRequest : Request {
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        if (collection.isEmpty()) return Response(true, "Коллекция пуста", false)
        val sum = collection.stream().mapToLong { it.employeesCount ?: 0 }.sum()
        val output = Messenger.message("Общее количество работников во всех организациях: ", TextColor.DEFAULT) +
                    Messenger.message("$sum", TextColor.BLUE)

        return Response(true, output, false)
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        throw CancellationException("Отмена запроса невозможна")
    }
}