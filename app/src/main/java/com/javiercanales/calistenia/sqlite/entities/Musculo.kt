package com.javiercanales.calistenia.sqlite.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "musculos")
data class Musculo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String
)
