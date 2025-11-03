package mx.com.virtualhand.zendo.domain

import java.util.*

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val time: String = "",
    val date: String = "",
    val category: String = "",
    val done: Boolean = false
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", "", false)
}


