package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pe.kabj.app_movil_kabj.databinding.FragmentWorkOrderDetailBinding

class WorkOrderDetailFragment : Fragment() {

    private var _binding: FragmentWorkOrderDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*
        @Suppress("DEPRECATION")
        arguments?.getParcelable<WorkOrderGeneralResponse>("workOrder")*/
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnReturn.setOnClickListener {
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
