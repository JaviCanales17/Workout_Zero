package com.javiercanales.calistenia.sqlite.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.javiercanales.calistenia.sqlite.entities.Dificultad
import com.javiercanales.calistenia.sqlite.entities.Ejercicio
import com.javiercanales.calistenia.sqlite.entities.Tipo
import com.javiercanales.calistenia.sqlite.entities.EjercicioMusculoCrossRef
import com.javiercanales.calistenia.sqlite.entities.EjercicioPlanesCrossRef
import com.javiercanales.calistenia.sqlite.entities.Musculo
import com.javiercanales.calistenia.sqlite.entities.Planes
import com.javiercanales.calistenia.sqlite.relations.EjercicioCompleto

@Dao
interface EjercicioDao {

    // Para insertar múltiples ejercicios
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(ejercicios: List<Ejercicio>)

    // Inserciones para datos de prepoblado
    @Insert
    suspend fun insertTipo(tipo: Tipo): Long

    @Insert
    suspend fun insertDificultad(dificultad: Dificultad): Long

    @Insert
    suspend fun insertMusculo(musculo: Musculo): Long

    @Insert
    suspend fun insertPlanes(planes: Planes): Long

    @Insert
    suspend fun insertEjercicio(ejercicio: Ejercicio): Long

    @Insert
    suspend fun insertEjercicioMusculoCrossRef(crossRef: EjercicioMusculoCrossRef)

    @Insert
    suspend fun insertEjercicioPlanesCrossRef(crossRef: EjercicioPlanesCrossRef)

    // Obtener todos los ejercicios
    @Transaction
    @Query("SELECT * FROM ejercicios")
    fun obtenerEjerciciosCompletos(): LiveData<List<EjercicioCompleto>>

    @Transaction
    @Query("SELECT * FROM ejercicios WHERE tipoId = :tipoId")
    fun obtenerEjerciciosPorTipo(tipoId: Int): LiveData<List<EjercicioCompleto>>

    @Transaction
    @Query("SELECT * FROM ejercicios WHERE dificultadId = :dificultadId")
    fun obtenerEjerciciosPorDificultad(dificultadId: Int): LiveData<List<EjercicioCompleto>>

    @Transaction
    @Query("SELECT * FROM ejercicios WHERE id IN (SELECT ejercicioId FROM ejercicio_musculo_cross_ref WHERE musculoId = :musculoId)")
    fun obtenerEjerciciosPorMusculo(musculoId: Int): LiveData<List<EjercicioCompleto>>

    @Transaction
    @Query("SELECT * FROM ejercicios WHERE id IN (SELECT ejercicioId FROM ejercicio_planes_cross_ref WHERE planesId = :planesId)")
    fun obtenerEjerciciosPorPlanes(planesId: Int): LiveData<List<EjercicioCompleto>>

    @Transaction
    @Query("SELECT * FROM ejercicios")
    suspend fun obtenerEjerciciosCompletosDirecto(): List<EjercicioCompleto>

    @Query("SELECT * FROM planes WHERE id = :planId")
    fun getPlanById(planId: Int): LiveData<Planes>

    @Query("SELECT * FROM planes WHERE id = :planId LIMIT 1")
    suspend fun getPlanesById(planId: Int): Planes?
}


