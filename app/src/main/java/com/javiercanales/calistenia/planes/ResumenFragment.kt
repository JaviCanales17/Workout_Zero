package com.javiercanales.calistenia.planes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.javiercanales.calistenia.R
import com.javiercanales.calistenia.databinding.FragmentResumenBinding
import com.javiercanales.calistenia.sqlite.database.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResumenFragment : Fragment(R.layout.fragment_resumen) {

    private var _binding: FragmentResumenBinding? = null
    private val binding get() = _binding!!

    private val args: ResumenFragmentArgs by navArgs()

    private var nombrePlan: String = ""   // <-- Variable para guardar el nombre
    private var kcalConsumidas: Double = 0.0  // <-- Variable para guardar las calorías

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.planesFragment, false)
        }

        val planId = args.planId
        val tiempoTotal = args.tiempoTotalReal
        val segundos = (tiempoTotal / 1000).toInt()

        val db = AppDatabase.getDatabase(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val plan = db.ejercicioDao().getPlanesById(planId)
            if (plan != null) {
                nombrePlan = plan.nombre  // <-- Guardar el nombre para Firebase
                binding.textNombrePlan.text = "Entrenamiento Finalizado:\n${plan.nombre}"
            } else {
                nombrePlan = getString(R.string.plan_desconocido)  // <-- También por si no hay nombre
                binding.textNombrePlan.text = getString(R.string.enhorabuena)
            }
        }

        binding.textDuracion.text = String.format(
            Locale.getDefault(),
            "Tiempo Total de Entrenamiento:\n%02d:%02d",
            (segundos / 60), (segundos % 60)
        )

        kcalConsumidas = if (segundos < 30) {
            2.0
        } else {
            (segundos / 30.0) * 4.0
        }

        binding.textCalorias.text = String.format(
            Locale.getDefault(),
            "Calorías Quemadas:\n~%.1f Kcal",
            kcalConsumidas
        )

        binding.btnFinalizar.setOnClickListener {
            Log.d("ResumenFragment", getString(R.string.click_en_boton_finalizar_detectado))

            val dbFirebase = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid

            if (userId == null) {
                Log.e("ResumenFragment", getString(R.string.usuario_no_autenticado))
                Toast.makeText(requireContext(),
                    getString(R.string.debes_estar_logueado), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val entrenamiento = hashMapOf(
                "fecha" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                "nombrePlan" to nombrePlan,
                "duracion" to tiempoTotal,
                "calorias" to kcalConsumidas
            )

            userId?.let { uid ->
                dbFirebase.collection(getString(R.string.historial))
                    .document(uid)
                    .collection(getString(R.string.entrenamientos))
                    .add(entrenamiento)
                    .addOnSuccessListener {
                        Log.d("ResumenFragment",
                            getString(R.string.entrenamiento_guardado_correctamente))
                        // Solo navegamos si guardar fue exitoso
                        findNavController().popBackStack(R.id.planesFragment, false)
                    }
                    .addOnFailureListener { e ->
                        Log.w("ResumenFragment", getString(R.string.error_al_guardar), e)
                        // Aquí podrías mostrar un Toast de error también
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

