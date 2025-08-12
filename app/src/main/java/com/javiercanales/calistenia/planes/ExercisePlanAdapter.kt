package com.javiercanales.calistenia.planes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.javiercanales.calistenia.databinding.ItemExercisePlanBinding
import com.javiercanales.calistenia.sqlite.relations.EjercicioCompleto

class ExercisePlanAdapter(private val exercises: List<EjercicioCompleto>) :
    RecyclerView.Adapter<ExercisePlanAdapter.ExerciseViewHolder>() {

    // ViewHolder que representa cada ejercicio.
    inner class ExerciseViewHolder(private val binding: ItemExercisePlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: EjercicioCompleto) {
            // Asigna los datos a las vistas del layout
            binding.nombre.text = exercise.ejercicio.nombre
            binding.descripcion.text = exercise.ejercicio.descripcion
        }
    }

    // Este metodo se llama para crear la vista del ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExercisePlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    // Este metodo se llama para vincular cada dato con su ViewHolder.
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    // Este metodo retorna el número total de elementos en la lista.
    override fun getItemCount(): Int = exercises.size
}
