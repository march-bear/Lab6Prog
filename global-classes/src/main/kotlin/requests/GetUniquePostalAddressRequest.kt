package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import iostreamers.Messenger
import iostreamers.TextColor
import kotlinx.serialization.Serializable

@Serializable
class GetUniquePostalAddressRequest : Request {
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        val setOfAddresses = collection.map { it.postalAddress.toString() }.toSet()
        if (setOfAddresses.isEmpty()) {
            return Response(
                true,
                "Коллекция пуста",
                false
            )
        }

        var output = Messenger.message("Уникальные ZIP-коды элементов:")

        setOfAddresses.forEach {
            output += Messenger.message("\n$it", TextColor.BLUE)
        }

        return Response(
            true,
            output,
            false
        )
    }

    override fun cancel(): String {
        throw CancellationException("Отмена запроса невозможна")
    }
}