package com.javiercanales.calistenia.sqlite.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.javiercanales.calistenia.sqlite.entities.Dificultad
import com.javiercanales.calistenia.sqlite.entities.Ejercicio
import com.javiercanales.calistenia.sqlite.entities.Tipo

data class EjercicioConDetalles(
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
    val dificultad: Dificultad
)
