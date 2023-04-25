package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable
import java.util.stream.Collectors

@Serializable
class GetUniquePostalAddressRequest : Request {
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        if (collection.isEmpty()) {
            return Response(
                true,
                "Коллекция пуста",
                false
            )
        }
        val setOfAddresses = collection.stream()
            .map { it.employeesCount }
            .filter { it != null }
            .collect(Collectors.toSet())
        var output = if (setOfAddresses.isNotEmpty()) {
            Messenger.message("Уникальные ZIP-коды элементов:")
        } else {
            Messenger.message("Все значения поля postalAddress являются null", TextColor.YELLOW)
        }

        setOfAddresses.forEach {
            output += Messenger.message("\n$it", TextColor.BLUE)
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