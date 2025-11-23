package mx.edu.laberinto_giroscopio.data.model

data class UserScore(
    val id: String? = null,
    val username: String,
    val score: Long // tiempo en milisegundos
)
