package mx.edu.laberinto_giroscopio.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.laberinto_giroscopio.data.model.UserScore
import mx.edu.laberinto_giroscopio.data.repository.GameRepository
import mx.edu.laberinto_giroscopio.ui.components.ScoresUiState

class ScoresViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState

    init {
        loadScores()
    }

    fun loadScores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.getScores()
            _uiState.value = result.fold(
                onSuccess = { list ->
                    ScoresUiState(isLoading = false, scores = list)
                },
                onFailure = { e ->
                    ScoresUiState(isLoading = false, errorMessage = e.message)
                }
            )
        }
    }

    fun addScore(username: String, score: Long) {
        viewModelScope.launch {
            val result = repository.addScore(username, score)
            result.fold(
                onSuccess = { newScore ->
                    _uiState.value =
                        _uiState.value.copy(scores = _uiState.value.scores + newScore)
                },
                onFailure = { e ->
                    _uiState.value =
                        _uiState.value.copy(errorMessage = e.message ?: "Error al agregar")
                }
            )
        }
    }

    fun updateScore(userScore: UserScore) {
        viewModelScope.launch {
            val result = repository.updateScore(userScore)
            result.fold(
                onSuccess = { updated ->
                    _uiState.value = _uiState.value.copy(
                        scores = _uiState.value.scores.map {
                            if (it.id == updated.id) updated else it
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.value =
                        _uiState.value.copy(errorMessage = e.message ?: "Error al actualizar")
                }
            )
        }
    }

    fun deleteScore(userScore: UserScore) {
        viewModelScope.launch {
            val result = repository.deleteScore(userScore)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        scores = _uiState.value.scores.filterNot { it.id == userScore.id }
                    )
                },
                onFailure = { e ->
                    _uiState.value =
                        _uiState.value.copy(errorMessage = e.message ?: "Error al eliminar")
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
