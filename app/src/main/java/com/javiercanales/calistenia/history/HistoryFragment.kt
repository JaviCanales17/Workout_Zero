package com.javiercanales.calistenia.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.javiercanales.calistenia.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.Locale

class HistoryFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var textSelectedDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var entrenamientoAdapter: EntrenamientoAdapter

    private val entrenamientos = mutableListOf<Entrenamiento>() // Lista de entrenamientos
    private val daysWithData = mutableSetOf<CalendarDay>() // Días con datos de Firebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        textSelectedDate = view.findViewById(R.id.textSelectedDate)
        recyclerView = view.findViewById(R.id.recyclerViewEntrenamientos)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        entrenamientoAdapter = EntrenamientoAdapter(entrenamientos)
        recyclerView.adapter = entrenamientoAdapter

        // Configurar el calendario
        calendarView.topbarVisible = true
        val today = CalendarDay.today()
        calendarView.setSelectedDate(today) // Seleccionar el día actual por defecto
        updateSelectedDate(today) // Actualizar el texto con el día actual

        // Cargar días con datos desde Firebase
        loadDataFromFirebase()

        // Cargar los entrenamientos del día actual
        loadEntrenamientosForDate(today)

        // Escuchar la selección de una fecha
        calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                updateSelectedDate(date)

                // Cargar los entrenamientos para el día seleccionado
                loadEntrenamientosForDate(date)
            }
        }

        return view
    }

    private fun loadDataFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        userId?.let {
            db.collection(getString(R.string.historial))
                .document(it)
                .collection(getString(R.string.entrenamientos))
                .get()
                .addOnSuccessListener { documents ->
                    // Limpiar el conjunto de días antes de agregar los nuevos días
                    daysWithData.clear()

                    // Recorrer los documentos obtenidos
                    for (document in documents) {
                        val fecha = document.getString(getString(R.string.fecha)) // Asegúrate de que este campo tenga el formato "YYYY-MM-DD"
                        fecha?.let {
                            val fechaParts = it.split("-")
                            val year = fechaParts[0].toInt()
                            val month = fechaParts[1].toInt() - 1 // Ajustar mes (Firebase da meses 1-based, CalendarDay usa 0-based)
                            val day = fechaParts[2].toInt()

                            // Crear CalendarDay y agregarlo al set
                            val calendarDay = CalendarDay.from(year, month, day)
                            daysWithData.add(calendarDay)
                        }
                    }

                    // Agregar el decorador para resaltar los días
                    val decorator = HighlightDaysDecorator(daysWithData, requireContext())
                    calendarView.addDecorator(decorator)
                }
                .addOnFailureListener { e ->
                    // Manejar el error de la consulta
                }
        }
    }

    private fun loadEntrenamientosForDate(date: CalendarDay) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        val fechaSeleccionada = String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d",
            date.year,
            date.month + 1, // Ajustar mes para que Firebase use el formato correcto
            date.day
        )

        userId?.let {
            db.collection(getString(R.string.historial))
                .document(it)
                .collection(getString(R.string.entrenamientos))
                .whereEqualTo(getString(R.string.fecha), fechaSeleccionada)
                .get()
                .addOnSuccessListener { documents ->
                    entrenamientos.clear() // Limpiar la lista de entrenamientos
                    for (document in documents) {
                        val nombrePlan = document.getString(getString(R.string.nombreplan)) ?: ""
                        val duracionLong = document.getLong(getString(R.string.duracion)) ?: 0L
                        val duracionTexto = convertirDuracion(duracionLong)
                        val calorias = document.getDouble(getString(R.string.calorias))?.toInt() ?: 0

                        entrenamientos.add(Entrenamiento(nombrePlan, duracionTexto, calorias))
                    }

                    // Notificar al adaptador que los datos han cambiado
                    entrenamientoAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Manejar el error de la consulta
                }
        }
    }

    override fun onResume() {
        super.onResume()

        // Recuperar el día actual
        val today = CalendarDay.today()

        // Establecer la fecha seleccionada como el día actual
        calendarView.setSelectedDate(today)

        // Actualizar el texto con el día actual
        updateSelectedDate(today)

        // Limpiar cualquier selección previa
        calendarView.clearSelection()
    }

    private fun updateSelectedDate(date: CalendarDay) {
        val dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", date.year, date.month + 1, date.day)
        textSelectedDate.text = "Día de Entrenamiento: $dateString"
    }

    private fun convertirDuracion(duracionMillis: Long): String {
        val segundos = (duracionMillis / 1000).toInt()
        val minutos = segundos / 60
        val segundosRestantes = segundos % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutos, segundosRestantes)
    }

    data class Entrenamiento(
        val nombrePlan: String,
        val duracion: String,
        val calorias: Int
    )
}
