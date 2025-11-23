package mx.edu.laberinto_giroscopio.data.network

import mx.edu.laberinto_giroscopio.data.model.UserScore

import retrofit2.Response
import retrofit2.http.*

interface GameApiService {
    @GET("scores")
    suspend fun getScores(): Response<List<UserScore>>

    @POST("scores")
    suspend fun addScore(@Body score: UserScore): Response<UserScore>

    @PUT("scores/{id}")
    suspend fun updateScore(
        @Path("id") id: String,
        @Body score: UserScore
    ): Response<UserScore>

    @DELETE("scores/{id}")
    suspend fun deleteScore(
        @Path("id") id: String
    ): Response<Unit>
}
