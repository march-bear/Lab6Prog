package requests

import CollectionController
import Organization
import collection.CollectionWrapper


/**
 * Интерфейс, реализуемый всеми запросами
 */
interface Request {
    /**
     * Обрабатывает запрос
     * @param collection коллекция, по отношению к которой обрабатывается запрос
     */
    fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response

    /**
     * Отменяет запрос
     */
    fun cancel(): String
}

data class Response(val requestCompleted: Boolean, val message: String, val archivable: Boolean = true)