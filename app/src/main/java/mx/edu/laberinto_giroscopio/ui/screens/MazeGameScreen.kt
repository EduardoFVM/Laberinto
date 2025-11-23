package mx.edu.laberinto_giroscopio.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import mx.edu.laberinto_giroscopio.maze.MazeView
import mx.edu.laberinto_giroscopio.viewmodel.ScoresViewModel
import kotlin.math.roundToInt

@Composable
fun MazeGameScreen(
    viewModel: ScoresViewModel,
    username: String
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val mazeViewState = remember { mutableStateOf<MazeView?>(null) }

    var lastScoreMillis by remember { mutableStateOf<Long?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Hola $username, inclina el dispositivo para mover la bola al punto verde.",
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MazeView(ctx).apply {
                        onWinListener = { time ->
                            lastScoreMillis = time
                            showDialog = true
                        }
                        resetGame()
                        mazeViewState.value = this
                    }
                },
                update = { view ->
                    view.onWinListener = { time ->
                        lastScoreMillis = time
                        showDialog = true
                    }
                    mazeViewState.value = view
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { mazeViewState.value?.resetGame() }) {
                Text("Reiniciar")
            }
            Button(onClick = { viewModel.loadScores() }) {
                Text("Recargar Scores")
            }
        }
    }

    // Manejo del sensor desde Compose
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    mazeViewState.value?.updatePhysics(pitch = y, roll = x)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    if (showDialog && lastScoreMillis != null) {
        SaveScoreDialog(
            timeMillis = lastScoreMillis!!,
            onSave = {
                // Usamos el username que ya recibió esta pantalla
                viewModel.addScore(username, lastScoreMillis!!)
                showDialog = false
                mazeViewState.value?.resetGame()
            },
            onDismiss = {
                showDialog = false
                mazeViewState.value?.resetGame()
            }
        )
    }
}

@Composable
fun SaveScoreDialog(
    timeMillis: Long,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val seconds = (timeMillis / 1000.0 * 100).roundToInt() / 100.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Ganaste!") },
        text = {
            Text(
                "Tu tiempo: $seconds segundos.\n" +
                        "Tu score se guardará con tu usuario actual."
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) {
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
