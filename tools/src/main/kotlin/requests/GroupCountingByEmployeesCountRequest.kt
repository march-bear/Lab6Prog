package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import java.util.stream.Collectors

class GroupCountingByEmployeesCountRequest : Request {
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        val map = collection.groupBy { it.employeesCount }
        if (map.isEmpty()) {
            return Response(
                true,
                "Коллекция пуста",
                false
            )
        }

        var output = ""
        map.forEach {
            output += Messenger.message("employeesCount=${it.key}: ", TextColor.DEFAULT)
            output += Messenger.message("${it.value.size}\n", TextColor.BLUE)
        }

        return Response(
            true,
            output,
            false
        )
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        throw CancellationException("Отмена запроса невозможна")
    }
}