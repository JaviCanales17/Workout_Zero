package com.javiercanales.calistenia.planes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.javiercanales.calistenia.R
import com.javiercanales.calistenia.databinding.FragmentPlanesBinding

class PlanesFragment : Fragment(R.layout.fragment_planes) {

    private var _binding: FragmentPlanesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configuramos los listeners para cada CardView
        binding.plan1Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 1)
            findNavController().navigate(action)
        }
        binding.plan2Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 2)
            findNavController().navigate(action)
        }
        binding.plan3Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 3)
            findNavController().navigate(action)
        }
        binding.plan4Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 4)
            findNavController().navigate(action)
        }
        binding.plan5Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 5)
            findNavController().navigate(action)
        }
        binding.plan6Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 6)
            findNavController().navigate(action)
        }
        binding.plan7Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 7)
            findNavController().navigate(action)
        }
        binding.plan8Card.setOnClickListener {
            val action = PlanesFragmentDirections.actionPlanesToDetallePlan(planId = 8)
            findNavController().navigate(action)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
