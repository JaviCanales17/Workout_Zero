package com.javiercanales.calistenia.sqlite.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dificultad")
data class Dificultad(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nivel: String
)
