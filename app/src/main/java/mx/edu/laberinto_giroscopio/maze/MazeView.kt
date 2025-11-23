package mx.edu.laberinto_giroscopio.maze

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

class MazeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var ballX = 0f
    var ballY = 0f
    private val ballRadius = 30f

    private var velX = 0f
    private var velY = 0f

    private val ballPaint = Paint().apply { color = Color.RED }
    private val wallPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 15f
    }
    private val goalPaint = Paint().apply { color = Color.GREEN }

    // paredes: left, top, right, bottom
    private val walls = mutableListOf<FloatArray>()

    private var cellW = 0f
    private var cellH = 0f

    private var goalX = 0f
    private var goalY = 0f

    var onWinListener: ((Long) -> Unit)? = null
    private var hasWon = false
    private var startTime: Long = System.currentTimeMillis()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // definimos una “rejilla” y construimos el laberinto a partir de ella
        val cols = 8
        val rows = 14
        cellW = w / cols.toFloat()
        cellH = h / rows.toFloat()

        walls.clear()

        fun addH(row: Int, fromCol: Int, toCol: Int) {
            val top = row * cellH
            walls.add(
                floatArrayOf(
                    fromCol * cellW,
                    top,
                    toCol * cellW,
                    top + wallPaint.strokeWidth
                )
            )
        }

        fun addV(col: Int, fromRow: Int, toRow: Int) {
            val left = col * cellW
            walls.add(
                floatArrayOf(
                    left,
                    fromRow * cellH,
                    left + wallPaint.strokeWidth,
                    toRow * cellH
                )
            )
        }

        // 🔹 Paredes horizontales
        addH(row = 2, fromCol = 0, toCol = 6)
        addH(row = 4, fromCol = 2, toCol = 8)
        addH(row = 6, fromCol = 0, toCol = 5)
        addH(row = 8, fromCol = 3, toCol = 8)
        addH(row = 10, fromCol = 0, toCol = 6)
        addH(row = 12, fromCol = 2, toCol = 8)

        // 🔹 Paredes verticales
        addV(col = 2, fromRow = 0, toRow = 4)
        addV(col = 5, fromRow = 2, toRow = 8)
        addV(col = 1, fromRow = 6, toRow = 12)
        addV(col = 7, fromRow = 4, toRow = 14)

        // bola al inicio (arriba-izquierda)
        ballX = cellW * 0.5f
        ballY = cellH * 1.5f

        // meta abajo-derecha
        goalX = cellW * (cols - 0.5f)
        goalY = cellH * (rows - 1.5f)

        startTime = System.currentTimeMillis()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // paredes
        for (wall in walls) {
            canvas.drawRect(wall[0], wall[1], wall[2], wall[3], wallPaint)
        }

        // meta
        canvas.drawCircle(goalX, goalY, 50f, goalPaint)

        // bola
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint)
    }

    fun updatePhysics(pitch: Float, roll: Float) {
        if (hasWon) return

        velX -= roll * 2f
        velY += pitch * 2f

        velX *= 0.9f
        velY *= 0.9f

        val nextX = ballX + velX
        val nextY = ballY + velY

        // bordes
        if (nextX < ballRadius || nextX > width - ballRadius) velX = -velX * 0.5f
        if (nextY < ballRadius || nextY > height - ballRadius) velY = -velY * 0.5f

        var collision = false
        for (wall in walls) {
            if (nextX + ballRadius > wall[0] && nextX - ballRadius < wall[2] &&
                nextY + ballRadius > wall[1] && nextY - ballRadius < wall[3]
            ) {
                collision = true
                velX = -velX * 0.5f
                velY = -velY * 0.5f
            }
        }

        if (!collision) {
            ballX += velX
            ballY += velY
        }

        val dx = ballX - goalX
        val dy = ballY - goalY
        if (sqrt(dx * dx + dy * dy) < 50 + ballRadius) {
            if (!hasWon) {
                hasWon = true
                val totalTime = System.currentTimeMillis() - startTime
                onWinListener?.invoke(totalTime)
            }
        }

        invalidate()
    }

    fun resetGame() {
        velX = 0f
        velY = 0f
        hasWon = false
        // si ya tenemos tamaño calculado, ponemos la bola otra vez en el inicio
        if (cellW > 0f && cellH > 0f) {
            ballX = cellW * 0.5f
            ballY = cellH * 1.5f
        }
        startTime = System.currentTimeMillis()
        invalidate()
    }
}
