package mx.edu.laberinto_giroscopio.data.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Desde el emulador, 10.0.2.2 apunta a tu PC
    private const val BASE_URL = "http://10.0.2.2:5000/"

    val api: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }
}
