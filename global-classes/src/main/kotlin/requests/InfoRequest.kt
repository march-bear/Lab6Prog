package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import iostreamers.Messenger
import iostreamers.TextColor
import kotlin.coroutines.cancellation.CancellationException

class InfoRequest : Request {
    override fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response {
        var output = Messenger.message("Информация о коллекции:\n")

        output += Messenger.message("-------------------------\n")

        output += Messenger.message("Тип коллекции: ")
        output += Messenger.message("${collection.getCollectionName()}\n", TextColor.BLUE)

        output += Messenger.message("Дата инициализации: ")
        output += Messenger.message("${collection.initializationDate}\n", TextColor.BLUE)

        output += Messenger.message("Количество элементов: ")
        output += Messenger.message("${collection.size}\n", TextColor.BLUE)

        output += Messenger.message("id максимального элемента: ")
        output += Messenger.message("${if (collection.isEmpty()) "<not found>" else collection.max().id}\n",
            TextColor.BLUE)

        output += Messenger.message("id минимального элемента: ")
        output += Messenger.message("${if (collection.isEmpty()) "<not found>" else collection.min().id}\n",
            TextColor.BLUE)
        output += Messenger.message("-------------------------\n")

        output += Messenger.message("\n\u00a9 ООО \"Мартовский Мишка\". Все права защищены от вас")

        return Response(true, output, false)
    }

    override fun cancel(): String {
        throw CancellationException("Отмена запроса невозможна")
    }
}