package requests

import CollectionController
import Organization
import collection.CollectionWrapper
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
        collection.stream().forEach {
            output += "\n------------------------"
            output += "\n" + it.toString()
            output += "\n------------------------"
        }

        return Response(true, output, false)
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        throw CancellationException("Отмена запроса невозможна")
    }

}