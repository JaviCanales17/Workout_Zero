package com.javiercanales.calistenia.planes

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import java.util.Locale


class CircularCountdownView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 70f
    }

    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 60f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 95f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private var progress = 1f  // 0 a 1
    private var tiempoTexto = "00:00"
    private var animator: ValueAnimator? = null
    private var duracionTotal = 0L
    private var tiempoRestante = 0L
    private var tiempoInicio = 0L
    private var isPaused = false
    private var tiempoPausaInicio: Long = 0L
    private var tiempoPausadoAcumulado: Long = 0L
    private var finalizadoManualmente = false
    private var onFinishAccion: (() -> Unit)? = null
    private var pulsingAnimator: ValueAnimator? = null


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = width.coerceAtMost(height) / 2f - 40f
        val centerX = width / 2f
        val centerY = height / 2f
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        val textY = centerY - (paintText.descent() + paintText.ascent()) / 2

        canvas.drawArc(rect, 0f, 360f, false, paintBackground)
        canvas.drawArc(rect, -90f, 360f * progress, false, paintProgress)
        canvas.drawText(tiempoTexto, centerX, textY, paintText)
    }

    fun iniciarAnimacion(duracion: Long, onFinish: () -> Unit) {
        duracionTotal = duracion
        tiempoInicio = System.currentTimeMillis()
        tiempoPausadoAcumulado = 0L
        isPaused = false
        finalizadoManualmente = false
        onFinishAccion = onFinish

        animator?.cancel()

        animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = duracion
            interpolator = android.view.animation.LinearInterpolator()
            addUpdateListener {
                if (!isPaused) {
                    val tiempoPasado = System.currentTimeMillis() - tiempoInicio - tiempoPausadoAcumulado
                    tiempoRestante = duracionTotal - tiempoPasado
                    progress = it.animatedValue as Float
                    actualizarTiempoTexto()
                    invalidate()
                }
            }
            doOnEnd {
                if (!finalizadoManualmente) {
                    pulsingAnimator?.cancel()
                    tiempoRestante = 0L
                    actualizarTiempoTexto()
                    invalidate()
                    onFinishAccion?.invoke()
                }
            }
            iniciarPulso()
            start()
        }
    }

    fun pausar() {
        if (!isPaused) {
            animator?.pause()
            tiempoPausaInicio = System.currentTimeMillis()
            isPaused = true
        }
    }

    fun reanudar() {
        if (isPaused) {
            animator?.resume()
            tiempoPausadoAcumulado += System.currentTimeMillis() - tiempoPausaInicio
            isPaused = false
        }
    }

    fun cancelar() {
        finalizadoManualmente = true
        pulsingAnimator?.cancel()
        animator?.cancel()
        animator = null
        progress = 0f
        tiempoRestante = 0L
        invalidate()
    }

    private fun actualizarTiempoTexto() {
        val segundos = (tiempoRestante / 1000).toInt()
        tiempoTexto = String.format(Locale.getDefault(), "%02d:%02d", (segundos / 60), (segundos % 60))
    }

    private fun iniciarPulso() {
        pulsingAnimator?.cancel()

        pulsingAnimator = ValueAnimator.ofFloat(55f, 65f).apply {
            duration = 1000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            addUpdateListener {
                paintProgress.strokeWidth = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

}
