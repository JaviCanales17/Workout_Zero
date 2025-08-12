package com.javiercanales.calistenia.sqlite.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "ejercicios",
    foreignKeys = [
        ForeignKey(entity = Tipo::class, parentColumns = ["id"], childColumns = ["tipoId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Dificultad::class, parentColumns = ["id"], childColumns = ["dificultadId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["tipoId"]),
        Index(value = ["dificultadId"])
    ]
)
data class Ejercicio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val tipoId: Int,
    val dificultadId: Int,
    val duracion: Int = 10, // en segundos
    val descanso: Int = 10, // en segundos
    val series: Int = 3
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
        parcel.writeInt(tipoId)
        parcel.writeInt(dificultadId)
        parcel.writeInt(duracion)
        parcel.writeInt(descanso)
        parcel.writeInt(series)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ejercicio> {
        override fun createFromParcel(parcel: Parcel): Ejercicio {
            return Ejercicio(parcel)
        }

        override fun newArray(size: Int): Array<Ejercicio?> {
            return arrayOfNulls(size)
        }
    }
}