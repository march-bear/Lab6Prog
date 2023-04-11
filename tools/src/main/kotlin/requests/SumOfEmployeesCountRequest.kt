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
        val sum = collection.sumOf { it.employeesCount ?: 0 }
        val output = if (sum == 0L)
            Messenger.message("Коллекция пуста", TextColor.BLUE)
        else
            Messenger.message("Общее количество работников во всех организациях: ", TextColor.DEFAULT) +
                    Messenger.message("$sum", TextColor.BLUE)

        return Response(true, output, false)
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        throw CancellationException("Отмена запроса невозможна")
    }
}