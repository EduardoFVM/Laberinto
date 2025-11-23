package mx.edu.laberinto_giroscopio.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.laberinto_giroscopio.data.model.UserScore
import mx.edu.laberinto_giroscopio.viewmodel.ScoresViewModel


@Composable
fun ScoresScreen(viewModel: ScoresViewModel) {
    val state by viewModel.uiState.collectAsState()

    var selectedScore by remember { mutableStateOf<UserScore?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.loadScores() }) {
                Text("Recargar")
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.scores) { score ->
                    ScoreItem(
                        userScore = score,
                        onClick = {
                            selectedScore = score
                            showEditDialog = true
                        },
                        onDelete = { viewModel.deleteScore(score) }
                    )
                }
            }
        }
    }

    state.errorMessage?.let { msg ->
        SnackbarHost(hostState = remember { SnackbarHostState() }) {
            Snackbar {
                Text(msg)
            }
        }
    }

    if (showEditDialog && selectedScore != null) {
        EditScoreDialog(
            userScore = selectedScore!!,
            onSave = { updated ->
                viewModel.updateScore(updated)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
fun ScoreItem(
    userScore: UserScore,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = userScore.username, style = MaterialTheme.typography.titleMedium)
                Text(text = "${userScore.score} ms", style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onDelete) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
fun EditScoreDialog(
    userScore: UserScore,
    onSave: (UserScore) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(userScore.username) }
    var scoreText by remember { mutableStateOf(userScore.score.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Score") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nombre") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = scoreText,
                    onValueChange = { scoreText = it },
                    label = { Text("Score (ms)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newScore = scoreText.toLongOrNull()
                if (newScore != null && username.isNotBlank()) {
                    onSave(
                        userScore.copy(
                            username = username.trim(),
                            score = newScore
                        )
                    )
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
