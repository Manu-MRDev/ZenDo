package mx.com.virtualhand.zendo.domain

import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val date: String
)
