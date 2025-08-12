package com.javiercanales.calistenia.profile

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.javiercanales.calistenia.LoginActivity
import com.javiercanales.calistenia.R
import com.javiercanales.calistenia.databinding.FragmentProfileBinding
import java.util.Calendar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var generoSeleccionado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Configurar rangos de SeekBars
        binding.seekEstatura.max = 60  // De 140 a 200
        binding.seekPeso.max = 100     // De 40 a 140

        // Listeners SeekBars
        binding.seekEstatura.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val estatura = 140 + progress
                binding.tvEstaturaValor.text = "Estatura: ${estatura} cm"
                calcularIMC()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekPeso.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val peso = 40 + progress
                binding.tvPesoValor.text = "Peso: ${peso} kg"
                calcularIMC()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Selección de género
        binding.btnGeneroHombre.setOnClickListener {
            seleccionarGenero(getString(R.string.hombre))
        }

        binding.btnGeneroMujer.setOnClickListener {
            seleccionarGenero(getString(R.string.mujer))
        }

        binding.btnEditar.setOnClickListener {
            habilitarCampos(true)
        }

        binding.btnConfirmar.setOnClickListener {
            guardarDatos()
        }

        cargarDatos()

        // Cargar preferencias
        val prefs = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE)
        val notificacionesActivas = prefs.getBoolean("notificaciones", true)

        binding.switchNotificaciones.isChecked = notificacionesActivas

        if (notificacionesActivas) {
            programarNotificaciones()
        }

        binding.switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notificaciones", isChecked).apply()

            if (isChecked) {
                programarNotificaciones()
                Toast.makeText(requireContext(),
                    getString(R.string.notificaciones_activadas), Toast.LENGTH_SHORT).show()
            } else {
                cancelarNotificaciones()
                Toast.makeText(requireContext(),
                    getString(R.string.notificaciones_desactivadas), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCerrarSesion.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }

    private fun seleccionarGenero(genero: String) {
        generoSeleccionado = genero

        val selectedBg = R.drawable.bg_genero_selected
        val defaultBg = R.drawable.bg_genero_default

        binding.btnGeneroHombre.setBackgroundResource(if (genero == getString(R.string.hombre)) selectedBg else defaultBg)
        binding.btnGeneroMujer.setBackgroundResource(if (genero == getString(R.string.mujer)) selectedBg else defaultBg)
    }

    private fun cargarDatos() {
        val uid = auth.currentUser?.uid ?: return

        db.collection(getString(R.string.usuarios)).document(uid).get()
            .addOnSuccessListener { doc ->
                doc?.let {
                    binding.etNombre.setText(it.getString(getString(R.string.nombre)))

                    when (it.getString(getString(R.string.genero))) {
                        getString(R.string.hombre) -> seleccionarGenero(getString(R.string.hombre))
                        getString(R.string.mujer) -> seleccionarGenero(getString(R.string.mujer))
                    }

                    it.getDouble(getString(R.string.estatura))?.toInt()?.let { estatura ->
                        binding.seekEstatura.progress = estatura - 140
                        binding.tvEstaturaValor.text = "Estatura: ${estatura} cm"
                    }

                    it.getDouble(getString(R.string.peso))?.toInt()?.let { peso ->
                        binding.seekPeso.progress = peso - 40
                        binding.tvPesoValor.text = "Peso: ${peso} kg"
                    }

                    calcularIMC()
                    habilitarCampos(false)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    getString(R.string.error_al_cargar_los_datos), Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarDatos() {
        val uid = auth.currentUser?.uid ?: return

        val nombre = binding.etNombre.text.toString().trim()
        val genero = generoSeleccionado ?: ""
        val estatura = 140 + binding.seekEstatura.progress
        val peso = 40 + binding.seekPeso.progress

        if (nombre.isEmpty() || genero.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.completa_todos_los_campos), Toast.LENGTH_SHORT).show()
            return
        }

        val datos = hashMapOf(
            getString(R.string.nombre) to nombre,
            getString(R.string.genero) to genero,
            getString(R.string.estatura) to estatura.toDouble(),
            getString(R.string.peso) to peso.toDouble()
        )

        db.collection(getString(R.string.usuarios)).document(uid).set(datos)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),
                    getString(R.string.datos_guardados), Toast.LENGTH_SHORT).show()
                habilitarCampos(false)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),
                    getString(R.string.error_al_guardar_1), Toast.LENGTH_SHORT).show()
            }
    }

    private fun calcularIMC() {
        val estatura = 140 + binding.seekEstatura.progress
        val peso = 40 + binding.seekPeso.progress

        if (estatura > 0) {
            val estaturaM = estatura / 100.0
            val imc = peso / (estaturaM * estaturaM)
            binding.tvIMC.text = "IMC: %.2f".format(imc)

            val (estado, colorRes) = when {
                imc < 18.5 -> getString(R.string.peso_bajo) to R.color.normal
                imc < 25 -> getString(R.string.peso_normal) to R.color.facil
                imc < 30 -> getString(R.string.sobrepeso) to R.color.media
                else -> getString(R.string.obesidad) to R.color.dificil
            }

            binding.tvEstadoIMC.text = estado
            val color = ContextCompat.getColor(requireContext(), colorRes)
            binding.tvEstadoIMC.setTextColor(color)
        }
    }

    private fun habilitarCampos(habilitar: Boolean) {
        binding.etNombre.isEnabled = habilitar
        binding.btnGeneroHombre.isEnabled = habilitar
        binding.btnGeneroMujer.isEnabled = habilitar
        binding.seekEstatura.isEnabled = habilitar
        binding.seekPeso.isEnabled = habilitar

        binding.btnEditar.visibility = if (habilitar) View.GONE else View.VISIBLE
        binding.btnConfirmar.visibility = if (habilitar) View.VISIBLE else View.GONE
    }

    private fun programarNotificaciones() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val horas = listOf(10, 17, 20) // Horas de las notificaciones

        for ((index, horaDelDia) in horas.withIndex()) {
            val intent = Intent(requireContext(), NotificacionReceiver::class.java)
            intent.putExtra("idNotificacion", index)

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                index, // requestCode único por cada hora
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val hora = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, horaDelDia)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_MONTH, 1) // Si la hora ya pasó hoy, programa para mañana
                }
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                hora.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    private fun cancelarNotificaciones() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (index in 0..2) {
            val intent = Intent(requireContext(), NotificacionReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    class NotificacionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "entrenamiento_reminder"

            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.recordatorios_de_entrenamiento),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.hora_de_entrenar))
                .setContentText(context.getString(R.string.no_olvides_completar_tu_rutina))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(1, notification)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
