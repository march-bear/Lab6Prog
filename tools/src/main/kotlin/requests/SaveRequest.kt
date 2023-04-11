package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import exceptions.CancellationException
import kotlinx.serialization.Serializable

@Serializable
class SaveRequest : Request {
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        try {
            cController.dataFileManager.saveData()
        } catch (ex: Exception) {
            return Response(
                false,
                "Ошибка во время сохранения коллекции. Сообщение ошибки: $ex",
                false
            )
        }

        return Response(
            true,
            message = "Коллекция сохранена",
            false
        )
    }

    override fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String {
        throw CancellationException("Отмена запроса невозможна")
    }
}