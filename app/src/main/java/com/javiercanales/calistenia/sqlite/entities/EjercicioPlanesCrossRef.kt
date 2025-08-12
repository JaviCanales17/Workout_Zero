package com.javiercanales.calistenia.sqlite.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "ejercicio_planes_cross_ref", primaryKeys = ["ejercicioId", "planesId"],
    foreignKeys = [
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Planes::class,
            parentColumns = ["id"],
            childColumns = ["planesId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ejercicioId"]),
        Index(value = ["planesId"])
    ]
)

data class EjercicioPlanesCrossRef(
    val ejercicioId: Int,
    val planesId: Int
)