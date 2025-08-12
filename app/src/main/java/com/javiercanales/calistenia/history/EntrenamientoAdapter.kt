package com.javiercanales.calistenia.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.javiercanales.calistenia.R

class EntrenamientoAdapter(
    private val entrenamientos: List<HistoryFragment.Entrenamiento>
) : RecyclerView.Adapter<EntrenamientoAdapter.EntrenamientoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntrenamientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entrenamiento, parent, false)
        return EntrenamientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntrenamientoViewHolder, position: Int) {
        val entrenamiento = entrenamientos[position]
        holder.bind(entrenamiento)
    }

    override fun getItemCount(): Int {
        return entrenamientos.size
    }

    inner class EntrenamientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNombrePlan: TextView = itemView.findViewById(R.id.textNombrePlan)
        private val textDuracion: TextView = itemView.findViewById(R.id.textDuracion)
        private val textCalorias: TextView = itemView.findViewById(R.id.textCalorias)

        fun bind(entrenamiento: HistoryFragment.Entrenamiento) {
            textNombrePlan.text = "Plan: ${entrenamiento.nombrePlan}"
            textDuracion.text = "Duración: ${entrenamiento.duracion}"
            textCalorias.text = "Calorías: ~${entrenamiento.calorias} Kcal"
        }
    }
}

