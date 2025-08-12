package com.javiercanales.calistenia.sqlite.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "ejercicio_musculo_cross_ref", primaryKeys = ["ejercicioId", "musculoId"],
    foreignKeys = [
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Musculo::class,
            parentColumns = ["id"],
            childColumns = ["musculoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ejercicioId"]),
        Index(value = ["musculoId"])
    ]
)

data class EjercicioMusculoCrossRef(
    val ejercicioId: Int,
    val musculoId: Int
)
