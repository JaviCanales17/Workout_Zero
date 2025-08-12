package com.javiercanales.calistenia.sqlite.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.javiercanales.calistenia.sqlite.entities.Ejercicio
import com.javiercanales.calistenia.sqlite.entities.EjercicioMusculoCrossRef
import com.javiercanales.calistenia.sqlite.entities.Planes

data class EjercicioConPlanes(
    @Embedded val ejercicio: Ejercicio,

    @Relation(
        parentColumn = "id",         // id de Ejercicio
        entityColumn = "id",         // id de Planes
        associateBy = Junction(
            EjercicioMusculoCrossRef::class,
            parentColumn = "ejercicioId",
            entityColumn = "planesId"
        )
    )
    val planess: List<Planes>
)
