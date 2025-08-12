package com.javiercanales.calistenia.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javiercanales.calistenia.R


class ExercisesFragment : Fragment() {

    private lateinit var viewModel: EjercicioViewModel
    private lateinit var adapter: ExerciseAdapter

    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerDificultad: Spinner
    private lateinit var spinnerMusculo: Spinner

    // Guardar la selección actual
    private var tipoSeleccionado: String? = null
    private var dificultadSeleccionada: String? = null
    private var musculoSeleccionado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercises, container, false)

        // RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ExerciseAdapter(emptyList())
        recyclerView.adapter = adapter

        // Spinners
        spinnerTipo = view.findViewById(R.id.spinnerTipo)
        spinnerDificultad = view.findViewById(R.id.spinnerDificultad)
        spinnerMusculo = view.findViewById(R.id.spinnerMusculo)

        // ViewModel
        viewModel = ViewModelProvider(this)[EjercicioViewModel::class.java]

        // Observar todos los ejercicios inicialmente
        viewModel.todosLosEjerciciosCompletos.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
        }

        configurarSpinners()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.planesFragment, false)
        }
    }

    private fun configurarSpinners() {
        val tipos = listOf(getString(R.string.todos),
            getString(R.string.fuerza), getString(R.string.cardio),
            getString(R.string.movilidad), getString(R.string.barras))
        val dificultades = listOf(getString(R.string.todas),
            getString(R.string.facil), getString(R.string.media), getString(R.string.dificil))
        val musculos = listOf(getString(R.string.todos),
            getString(R.string.biceps),
            getString(R.string.triceps),
            getString(R.string.pectoral),
            getString(R.string.abdomen), getString(R.string.hombros),
            getString(R.string.espalda), getString(R.string.piernas))

        // Adapter para Tipo
        val adapterTipo = ArrayAdapter(requireContext(), R.layout.spinner_item, tipos)
        adapterTipo.setDropDownViewResource(R.layout.spinner_item)
        spinnerTipo.adapter = adapterTipo

        // Adapter para Dificultad
        val adapterDificultad = ArrayAdapter(requireContext(), R.layout.spinner_item, dificultades)
        adapterDificultad.setDropDownViewResource(R.layout.spinner_item)
        spinnerDificultad.adapter = adapterDificultad

        // Adapter para Músculo
        val adapterMusculo = ArrayAdapter(requireContext(), R.layout.spinner_item, musculos)
        adapterMusculo.setDropDownViewResource(R.layout.spinner_item)
        spinnerMusculo.adapter = adapterMusculo

        // Escuchas
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                tipoSeleccionado = if (pos == 0) null else tipos[pos]
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerDificultad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                dificultadSeleccionada = if (pos == 0) null else dificultades[pos]
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerMusculo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                musculoSeleccionado = if (pos == 0) null else musculos[pos]
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun aplicarFiltros() {
        // Se puede combinar con lógica más compleja si se quiere aplicar filtros múltiples a la vez.
        viewModel.filtrarEjercicios(
            tipoSeleccionado,
            dificultadSeleccionada,
            musculoSeleccionado
        ).observe(viewLifecycleOwner) { listaFiltrada ->
            adapter.actualizarLista(listaFiltrada)
        }
    }
}


