package mx.com.virtualhand.zendo.domain

import java.util.*

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val date: String = ""
) {
    // 🔹 Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "")
}

