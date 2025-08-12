package com.javiercanales.calistenia.exercises

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.javiercanales.calistenia.R
import com.javiercanales.calistenia.sqlite.relations.EjercicioCompleto

class ExerciseAdapter(private var ejercicios: List<EjercicioCompleto>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val description: TextView = view.findViewById(R.id.tvDescription)
        val tipo: TextView = view.findViewById(R.id.tvType)
        val dificultad: TextView = view.findViewById(R.id.tvDificulty)
        val musculos: TextView = view.findViewById(R.id.tvMuscle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val ejercicioCompleto = ejercicios[position]
        holder.title.text = ejercicioCompleto.ejercicio.nombre
        holder.description.text = ejercicioCompleto.ejercicio.descripcion
        holder.tipo.text = "Tipo de Ejercicio: ${ejercicioCompleto.tipo.nombre}"
        holder.dificultad.text = "Dificultad ${ejercicioCompleto.dificultad.nivel}"
        holder.musculos.text = "Grupos Musculares: ${ejercicioCompleto.musculos.joinToString { it.nombre }}"

        // Cambiar color de texto según la dificultad
        val contexto = holder.itemView.context
        when (ejercicioCompleto.dificultad.nivel.lowercase()) {
            "fácil" -> holder.dificultad.setTextColor(ContextCompat.getColor(contexto,
                R.color.facil
            ))
            "media" -> holder.dificultad.setTextColor(ContextCompat.getColor(contexto,
                R.color.media
            ))
            "difícil" -> holder.dificultad.setTextColor(ContextCompat.getColor(contexto,
                R.color.dificil
            ))
            else -> holder.dificultad.setTextColor(ContextCompat.getColor(contexto, R.color.black))
        }
    }

    override fun getItemCount(): Int = ejercicios.size

    fun actualizarLista(nuevaListaCompleta: List<EjercicioCompleto>) {
        ejercicios = nuevaListaCompleta
        notifyDataSetChanged()
    }

}