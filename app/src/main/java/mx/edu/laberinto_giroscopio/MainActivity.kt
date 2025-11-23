package mx.edu.laberinto_giroscopio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.laberinto_giroscopio.data.network.RetrofitClient
import mx.edu.laberinto_giroscopio.data.repository.GameRepository
import mx.edu.laberinto_giroscopio.ui.screens.LaberintoGiroscopioApp
import mx.edu.laberinto_giroscopio.ui.theme.LaberintoGiroscopioTheme
import mx.edu.laberinto_giroscopio.viewmodel.ScoresViewModel
import mx.edu.laberinto_giroscopio.viewmodel.ScoresViewModelFactory


class MainActivity : ComponentActivity() {

    private val viewModel: ScoresViewModel by viewModels {
        ScoresViewModelFactory(GameRepository(RetrofitClient.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaberintoGiroscopioTheme {
                LaberintoGiroscopioApp(viewModel = viewModel)
            }
        }
    }
}
