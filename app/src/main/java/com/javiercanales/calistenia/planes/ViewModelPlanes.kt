package com.javiercanales.calistenia.planes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.javiercanales.calistenia.sqlite.dao.EjercicioDao
import com.javiercanales.calistenia.sqlite.database.AppDatabase
import com.javiercanales.calistenia.sqlite.entities.Planes
import com.javiercanales.calistenia.sqlite.relations.EjercicioCompleto

class DetallePlanViewModel(application: Application) : AndroidViewModel(application) {

    private val ejercicioDao = AppDatabase.getDatabase(application).ejercicioDao()
    private val planDao: EjercicioDao = AppDatabase.getDatabase(application).ejercicioDao()

    fun getEjerciciosPlanes(planId: Int): LiveData<List<EjercicioCompleto>> {
        return ejercicioDao.obtenerEjerciciosPorPlanes(planId)
    }
    fun getPlan(planId: Int): LiveData<Planes> {
        return planDao.getPlanById(planId)
    }
}

