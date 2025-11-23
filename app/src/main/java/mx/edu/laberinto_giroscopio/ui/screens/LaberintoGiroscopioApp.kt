package mx.edu.laberinto_giroscopio.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import mx.edu.laberinto_giroscopio.viewmodel.ScoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaberintoGiroscopioApp(viewModel: ScoresViewModel) {

    var username by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    if (username == null) {
        LoginScreen(
            onLoginSuccess = { user ->
                username = user
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (selectedTab == 0)
                                "Laberinto Gyro Game - $username"
                            else
                                "Scores (CRUD) - $username"
                        )
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                // cerrar sesión
                                username = null
                                selectedTab = 0
                            }
                        ) {
                            Text("Cerrar sesión")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Juego") },
                        icon = {}
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Scores") },
                        icon = {}
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedTab) {
                    0 -> MazeGameScreen(
                        viewModel = viewModel,
                        username = username!!
                    )
                    1 -> ScoresScreen(viewModel = viewModel)
                }
            }
        }
    }
}
