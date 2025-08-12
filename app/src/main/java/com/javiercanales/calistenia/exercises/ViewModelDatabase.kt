package com.javiercanales.calistenia.exercises

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.javiercanales.calistenia.sqlite.database.AppDatabase
import com.javiercanales.calistenia.sqlite.relations.EjercicioCompleto

class EjercicioViewModel(application: Application) : AndroidViewModel(application) {

    private val ejercicioDao = AppDatabase.getDatabase(application).ejercicioDao()

    // LiveData para todos los ejercicios (relaciones completas)
    val todosLosEjerciciosCompletos: LiveData<List<EjercicioCompleto>> = ejercicioDao.obtenerEjerciciosCompletos()

    // Filtrado combinado por tipo, dificultad y musculo
    fun filtrarEjercicios(tipo: String?, dificultad: String?, musculo: String?): LiveData<List<EjercicioCompleto>> {
        return liveData {
            val ejercicios = ejercicioDao.obtenerEjerciciosCompletosDirecto() // función suspendida del DAO
            val filtrados = ejercicios.filter { ejercicio ->
                val coincideTipo = tipo == null || ejercicio.tipo.nombre == tipo
                val coincideDificultad = dificultad == null || ejercicio.dificultad.nivel == dificultad
                val coincideMusculo = musculo == null || ejercicio.musculos.any { it.nombre == musculo }

                coincideTipo && coincideDificultad && coincideMusculo
            }
                .distinctBy { it.ejercicio.nombre } //Eliminar duplicados por nombre.
                .sortedBy { it.ejercicio.nombre } //Ordenar alfabeticamente.
            emit(filtrados)
        }
    }
}
