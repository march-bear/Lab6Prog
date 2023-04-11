package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import command.CommandResult
import exceptions.CancellationException
import kotlinx.serialization.Serializable

@Serializable
class ShowRequest : Request {
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        if (collection.isEmpty()) {
            return Response(
                true,
                "Коллекция пуста",
                false,
            )
        }

        var output = "Элементы коллекции:"
        collection.forEach {
            output += "\n------------------------"
            output += "\n" + it.toString()
            output += "\n------------------------"
        }

        return Response(true, output, false)
    }

    override fun cancel(): String {
        throw CancellationException("Отмена запроса невозможна")
    }

}