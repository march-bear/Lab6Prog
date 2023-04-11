package requests

import CollectionController
import Organization
import collection.CollectionWrapper
import kotlinx.serialization.Serializable


/**
 * Интерфейс, реализуемый всеми запросами
 */

@Serializable
sealed interface Request {
    /**
     * Обрабатывает запрос
     * @param collection коллекция, по отношению к которой обрабатывается запрос
     */
    fun process(collection: CollectionWrapper<Organization>, cController: CollectionController): Response

    /**
     * Отменяет запрос
     */
    fun cancel(collection: CollectionWrapper<Organization>, cController: CollectionController): String
}

@Serializable
data class Response(val requestCompleted: Boolean, val message: String, val archivable: Boolean = true)