package com.javiercanales.calistenia.planes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.javiercanales.calistenia.R
import com.javiercanales.calistenia.databinding.FragmentDetallePlanBinding
import com.javiercanales.calistenia.sqlite.entities.Ejercicio

class DetallePlanFragment : Fragment(R.layout.fragment_detalle_plan) {

    private val args: DetallePlanFragmentArgs by navArgs()
    private lateinit var viewModel: DetallePlanViewModel
    private var ejercicios: List<Ejercicio> = emptyList()

    private var _binding: FragmentDetallePlanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallePlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DetallePlanViewModel::class.java]

        val planId = args.planId

        viewModel.getPlan(planId).observe(viewLifecycleOwner) { plan ->
            binding.nombrePlan.text = plan.nombre
            binding.descripcionPlan.text = plan.descripcion
            binding.duracionPlan.text = "Duración: ~${plan.duracion}min."
        }

        viewModel.getEjerciciosPlanes(planId).observe(viewLifecycleOwner) { ejerciciosPlan ->
            ejercicios = ejerciciosPlan.map { it.ejercicio }
            val adapter = ExercisePlanAdapter(ejerciciosPlan)
            binding.recyclerEjercicios.adapter = adapter
            binding.recyclerEjercicios.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.btnIniciar.setOnClickListener {
            val action = DetallePlanFragmentDirections
                .actionDetallePlanToEntrenamiento(planId, ejercicios.toTypedArray())
            findNavController().navigate(action)
        }

    }
}
