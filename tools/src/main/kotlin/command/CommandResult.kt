package command

import requests.Request

data class CommandResult(
    val commandCompleted: Boolean,
    val request: Request? = null,
    val message: String? = null,
)