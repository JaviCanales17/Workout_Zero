package com.javiercanales.calistenia.sqlite.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.javiercanales.calistenia.sqlite.entities.Ejercicio
import com.javiercanales.calistenia.sqlite.entities.EjercicioMusculoCrossRef
import com.javiercanales.calistenia.sqlite.entities.Musculo

data class EjercicioConMusculos(
    @Embedded val ejercicio: Ejercicio,

    @Relation(
        parentColumn = "id",         // id de Ejercicio
        entityColumn = "id",         // id de Musculo
        associateBy = Junction(
            EjercicioMusculoCrossRef::class,
            parentColumn = "ejercicioId",
            entityColumn = "musculoId"
        )
    )
    val musculos: List<Musculo>
)
