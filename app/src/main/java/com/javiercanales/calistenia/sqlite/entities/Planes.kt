package com.javiercanales.calistenia.sqlite.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planes")
data class Planes(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val duracion: String
)