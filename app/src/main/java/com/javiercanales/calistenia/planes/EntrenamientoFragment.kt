package com.javiercanales.calistenia.planes

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.javiercanales.calistenia.R
import com.javiercanales.calistenia.databinding.FragmentEntrenamientoBinding
import com.javiercanales.calistenia.sqlite.entities.Ejercicio

@Suppress("DEPRECATION")
class EntrenamientoFragment : Fragment(R.layout.fragment_entrenamiento) {

    private var _binding: FragmentEntrenamientoBinding? = null
    private val binding get() = _binding!!

    private val args: EntrenamientoFragmentArgs by navArgs()

    private lateinit var ejercicios: List<Ejercicio>
    private var ejercicioIndex = 0
    private var serie = 1
    private var enDescanso = false
    private var enPausa = false
    private var enPreparacion = true
    private var tiempoInicioEntrenamiento: Long = 0L
    private var tiempoRestante = 0L
    private var onFinishAccion: (() -> Unit)? = null

    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEntrenamientoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ejercicios = args.ejercicios.toList()
        tiempoInicioEntrenamiento = System.currentTimeMillis()

        setupBotones()
        iniciarCuentaAtrasInicial()

        val powerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "CalisteniaWorkoutZero:EntrenamientoWakeLock")
        wakeLock.acquire(40 * 60 * 1000L)
    }

    private fun setupBotones() {
        binding.btnPausar.setOnClickListener {
            if (enPausa) {
                binding.circularCountdownView.reanudar()
            } else {
                binding.circularCountdownView.pausar()
            }
            enPausa = !enPausa
            binding.btnPausar.text = if (enPausa) getString(R.string.reanudar) else getString(R.string.pausar)
        }

        binding.btnSiguiente.setOnClickListener {
            binding.circularCountdownView.cancelar()

            if (enPreparacion) {
                enPreparacion = false
                iniciarEjercicio()
            } else {
                avanzarEjercicio()
            }

            enPausa = false
            binding.btnPausar.text = getString(R.string.pausar)
        }

        binding.btnAnterior.setOnClickListener {
            binding.circularCountdownView.cancelar()

            if (enPreparacion) {
                enPausa = false
                binding.btnPausar.text = getString(R.string.pausar)
                iniciarCuentaAtrasInicial()
                return@setOnClickListener
            }

            if (ejercicioIndex == 0) {
                ejercicioIndex = 0
                serie = 1
                enDescanso = false
                iniciarEjercicio()
            } else {
                retrocederEjercicio()
            }

            enPausa = false
            binding.btnPausar.text = getString(R.string.pausar)
        }
    }

    private fun iniciarCuentaAtrasInicial() {
        binding.textEjercicioActual.text = getString(R.string.preparate)
        binding.textDescripcion.text = getString(R.string.descripcion_calentamiento)

        iniciarTemporizador(16000L) {
            enPreparacion = false
            tiempoInicioEntrenamiento = System.currentTimeMillis()
            iniciarEjercicio()
        }
    }

    private fun iniciarEjercicio() {
        binding.tvSerie.text = "Serie: $serie/3"

        if (ejercicioIndex >= ejercicios.size) {
            val planId = args.planId
            val tiempoTotalReal = System.currentTimeMillis() - tiempoInicioEntrenamiento
            val action = EntrenamientoFragmentDirections.actionEntrenamientoToResumen(planId, tiempoTotalReal)
            findNavController().navigate(action)
            return
        }

        val ejercicio = ejercicios[ejercicioIndex]
        binding.textEjercicioActual.text = if (enDescanso) "Descanso" else "Ejercicio:\n${ejercicio.nombre}"
        binding.textDescripcion.text = if (enDescanso) "Tiempo de descanso:\n¡Recupérate bien!" else ejercicio.descripcion

        val duracion = if (enDescanso) 46000L else 61000L

        iniciarTemporizador(duracion) {
            vibrar()
            if (!enDescanso) {
                enDescanso = true
                iniciarEjercicio()
            } else {
                if (serie < 3) {
                    serie++
                    enDescanso = false
                    iniciarEjercicio()
                } else {
                    avanzarEjercicio()
                }
            }
        }
    }

    private fun iniciarTemporizador(duracion: Long, onFinish: () -> Unit = {}) {
        tiempoRestante = duracion
        onFinishAccion = onFinish

        binding.circularCountdownView.cancelar()
        binding.circularCountdownView.iniciarAnimacion(duracion, onFinish)
    }

    private fun avanzarEjercicio() {
        if (ejercicioIndex < ejercicios.size) {
            ejercicioIndex++
            serie = 1
            enDescanso = false
            iniciarEjercicio()
        }
    }

    private fun retrocederEjercicio() {
        if (ejercicioIndex > 0) {
            ejercicioIndex--
            serie = 1
            enDescanso = false
            iniciarEjercicio()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.circularCountdownView.cancelar()
        wakeLock.release()
        _binding = null
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibrar() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
