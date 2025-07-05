package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.data.dto.OperationResult
import pe.kabj.app_movil_kabj.databinding.FragmentConsultWorkOrderBinding
import pe.kabj.app_movil_kabj.presentation.utils.ModalDialogUtils
import pe.kabj.app_movil_kabj.util.extensions.getTrimmedText
import pe.kabj.app_movil_kabj.util.extensions.validateIsNumber
import pe.kabj.app_movil_kabj.util.extensions.validateNotEmpty
import pe.kabj.app_movil_kabj.presentation.viewmodels.ConsultWorkOrderViewModel
import kotlin.getValue

class ConsultWorkOrderFragment : Fragment() {

    private var _binding: FragmentConsultWorkOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ConsultWorkOrderViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ConsultWorkOrderViewModel(requireContext()) as T
            }
        }
    }

    private lateinit var alertDialog : AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentConsultWorkOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        alertDialog = ModalDialogUtils.createProgressDialog(requireContext(), "Buscando orden de trabajo . . .")
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        binding.txtNumberWorkOrder.addTextChangedListener {
            if (!it.isNullOrBlank()){
                binding.layoutTxtNumberWorkOrder.error = null
            }
        }

        binding.btnSearchWorkOrder.setOnClickListener {
            // 64618576
            // Validar que el campo sea un numero
            val isNumberWorkOrderValid = binding.layoutTxtNumberWorkOrder.validateIsNumber(getString(R.string.msg_error_number_work_order_invalid))

            // Si el campo no es un numero, no continuar
            if (!isNumberWorkOrderValid) {
                ModalDialogUtils.showFailureDialog(requireContext(), "Error de Tipeo", "Debe ingresar un número")
                return@setOnClickListener
            }

            val numberWorkOrder : Long = binding.txtNumberWorkOrder.getTrimmedText().toLong()

            viewModel.searchWorkOrderBy(numberWorkOrder)

        }

    }

    private fun setupObservers() {
        viewModel.workOrder.observe(viewLifecycleOwner) { workOrder ->

            if (workOrder != null){

                val fragment = WorkOrderDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("workOrder", workOrder)
                    }

                }

                parentFragmentManager.beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack("WorkOrderDetail")
                    .commit()
            } else {
                ModalDialogUtils.showFailureDialog(
                    requireContext(),
                    "Ordern no encontrada",
                    "No se encontró la orden de trabajo: " + binding.txtNumberWorkOrder.getTrimmedText()
                )
            }

        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            if (isSearching){
                alertDialog.show()
            } else {
                alertDialog.dismiss()
            }
        }

        viewModel.isEnabledBtnSearch.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnSearchWorkOrder.isEnabled = isEnabled
        }

        viewModel.messageResult.observe(viewLifecycleOwner) { messageResult ->

            if (messageResult is OperationResult.Error){
                ModalDialogUtils.showFailureDialog(requireContext(), messageResult.title, messageResult.message)
            }

        }

    }

}
