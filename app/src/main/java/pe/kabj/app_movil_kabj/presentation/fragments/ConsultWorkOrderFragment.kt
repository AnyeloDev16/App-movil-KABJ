package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.databinding.FragmentConsultWorkOrderBinding
import pe.kabj.app_movil_kabj.util.extensions.getTrimmedText
import pe.kabj.app_movil_kabj.util.extensions.validateIsNumber
import pe.kabj.app_movil_kabj.util.extensions.validateNotEmpty
import pe.kabj.app_movil_kabj.presentation.viewmodels.ConsultWorkOrderViewModel

class ConsultWorkOrderFragment : Fragment() {

    private var _binding: FragmentConsultWorkOrderBinding? = null
    private val binding get() = _binding!!

    private val consultWorkOrderViewModel: ConsultWorkOrderViewModel by activityViewModels()

    // Components
    private lateinit var layoutTxtNumberWorkOrder: TextInputLayout
    private lateinit var txtNumberWorkOrder: TextInputEditText
    private lateinit var btnSearchWorkOrder: MaterialButton

    private var progressDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentConsultWorkOrderBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupComponents()
        setupListeners()
        setupObservers()
    }

    private fun setupComponents() {
        layoutTxtNumberWorkOrder = binding.layoutTxtNumberWorkOrder
        txtNumberWorkOrder = binding.txtNumberWorkOrder
        btnSearchWorkOrder = binding.btnSearchWorkOrder
    }

    private fun setupListeners() {
        txtNumberWorkOrder.addTextChangedListener {
            if (!it.isNullOrBlank()){
                layoutTxtNumberWorkOrder.error = null
            }
        }

        btnSearchWorkOrder.setOnClickListener {

            // Validar que el campo no este vacio
            val isNumberWorkOrderNotEmpty = layoutTxtNumberWorkOrder.validateNotEmpty(getString(R.string.msg_error_number_work_order_required))

            // Si el campo esta vacio, no continuar
            if (!isNumberWorkOrderNotEmpty) return@setOnClickListener

            // Validar que el campo sea un numero
            val isNumberWorkOrderValid = layoutTxtNumberWorkOrder.validateIsNumber(getString(R.string.msg_error_number_work_order_invalid))

            // Si el campo no es un numero, no continuar
            if (!isNumberWorkOrderValid) return@setOnClickListener

            val numberWorkOrder : Long = txtNumberWorkOrder.getTrimmedText().toLong()

            consultWorkOrderViewModel.searchWorkOrderBy(numberWorkOrder)

        }
    }

    private fun setupObservers() {
        consultWorkOrderViewModel.workOrder.observe(viewLifecycleOwner, Observer { workOrder ->

            if (workOrder != null){
                parentFragmentManager.beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, WorkOrderDetailFragment())
                    .addToBackStack("WorkOrderDetail")
                    .commit()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Orden de trabajo")
                    .setMessage("No se encontró la orden de trabajo: " + txtNumberWorkOrder.getTrimmedText())
                    .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                    .show()
            }

        })

        consultWorkOrderViewModel.isSearching.observe(viewLifecycleOwner, Observer { isSearching ->

            if (isSearching) {
                showLoading()
            } else {
                hideLoading()
            }

        })
    }

    private fun showLoading() {
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)

        progressDialog = MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setView(view)
            .create()

        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }

}
