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
        if (collection.isEmpty()) {
            return Response(
                true,
                "Коллекция пуста",
                false
            )
        }
        var output = ""
        collection.stream()
            .collect(Collectors.groupingBy { it.employeesCount ?: -1 })
            .forEach {
                output += Messenger.message("employeesCount=${if (it.key != -1L) it.key else null}: ", TextColor.DEFAULT)
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