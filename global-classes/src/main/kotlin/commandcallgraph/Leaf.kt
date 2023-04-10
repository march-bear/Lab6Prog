package commandcallgraph

import requests.Request

data class Leaf(
    val request: Request?,
    val id: String,
    val previousLeaf: Leaf?,
)