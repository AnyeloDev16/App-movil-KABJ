package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import pe.kabj.app_movil_kabj.databinding.FragmentWorkOrderDetailBinding
import pe.kabj.app_movil_kabj.presentation.viewmodels.ConsultWorkOrderViewModel
import pe.kabj.app_movil_kabj.presentation.viewmodels.WorkOrderDetailViewModel

class WorkOrderDetailFragment : Fragment() {

    private var _binding: FragmentWorkOrderDetailBinding? = null
    private val binding get() = _binding!!

    private val workOrderDetailViewModel: WorkOrderDetailViewModel by viewModels()
    private val consultWorkOrderViewModel: ConsultWorkOrderViewModel by viewModels()

    private lateinit var btnReturn: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupComponents()
        setupListeners()
    }

    private fun setupComponents() {
        btnReturn = binding.btnReturn
    }

    private fun setupListeners() {
        btnReturn.setOnClickListener {
            handleBackPress()
        }
    }

    private fun handleBackPress() {
        parentFragmentManager.popBackStack()
        /*
        if (workOrderDetailViewModel.hayCambios()) {
            // Mostrar un diálogo de confirmación antes de regresar
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cambios no guardados")
                .setMessage("¿Desea salir sin guardar los cambios?")
                .setPositiveButton("Salir") { _, _ ->
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } else {
            // No hay cambios → salir directamente
            parentFragmentManager.popBackStack()
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
