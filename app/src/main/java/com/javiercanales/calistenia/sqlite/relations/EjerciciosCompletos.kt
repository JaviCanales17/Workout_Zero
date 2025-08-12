package com.javiercanales.calistenia.sqlite.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.javiercanales.calistenia.sqlite.entities.Dificultad
import com.javiercanales.calistenia.sqlite.entities.Ejercicio
import com.javiercanales.calistenia.sqlite.entities.EjercicioMusculoCrossRef
import com.javiercanales.calistenia.sqlite.entities.EjercicioPlanesCrossRef
import com.javiercanales.calistenia.sqlite.entities.Musculo
import com.javiercanales.calistenia.sqlite.entities.Planes
import com.javiercanales.calistenia.sqlite.entities.Tipo

data class EjercicioCompleto(
    @Embedded val ejercicio: Ejercicio,

    @Relation(
        parentColumn = "tipoId",
        entityColumn = "id"
    )
    val tipo: Tipo,

    @Relation(
        parentColumn = "dificultadId",
        entityColumn = "id"
    )
    val dificultad: Dificultad,

    @Relation(
        entity = Musculo::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EjercicioMusculoCrossRef::class,
            parentColumn = "ejercicioId",
            entityColumn = "musculoId"
        )
    )
    val musculos: List<Musculo>,

    @Relation(
        entity = Planes::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EjercicioPlanesCrossRef::class,
            parentColumn = "ejercicioId",
            entityColumn = "planesId"
        )
    )
    val planes: List<Planes>
)
