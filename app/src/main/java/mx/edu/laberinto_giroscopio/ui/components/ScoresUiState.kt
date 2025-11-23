package mx.edu.laberinto_giroscopio.ui.components

import mx.edu.laberinto_giroscopio.data.model.UserScore


data class ScoresUiState(
    val isLoading: Boolean = false,
    val scores: List<UserScore> = emptyList(),
    val errorMessage: String? = null
)
