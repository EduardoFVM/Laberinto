package mx.edu.laberinto_giroscopio.data.repository

import mx.edu.laberinto_giroscopio.data.model.UserScore
import mx.edu.laberinto_giroscopio.data.network.GameApiService


class GameRepository(private val api: GameApiService) {

    suspend fun getScores(): Result<List<UserScore>> = try {
        val resp = api.getScores()
        if (resp.isSuccessful) {
            Result.success(resp.body().orEmpty())
        } else {
            Result.failure(Exception("Error ${resp.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addScore(username: String, score: Long): Result<UserScore> = try {
        val resp = api.addScore(UserScore(username = username, score = score))
        if (resp.isSuccessful && resp.body() != null) {
            Result.success(resp.body()!!)
        } else {
            Result.failure(Exception("Error ${resp.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateScore(userScore: UserScore): Result<UserScore> = try {
        val id = userScore.id ?: return Result.failure(Exception("ID nulo"))
        val resp = api.updateScore(id, userScore)
        if (resp.isSuccessful && resp.body() != null) {
            Result.success(resp.body()!!)
        } else {
            Result.failure(Exception("Error ${resp.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteScore(userScore: UserScore): Result<Unit> = try {
        val id = userScore.id ?: return Result.failure(Exception("ID nulo"))
        val resp = api.deleteScore(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error ${resp.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
