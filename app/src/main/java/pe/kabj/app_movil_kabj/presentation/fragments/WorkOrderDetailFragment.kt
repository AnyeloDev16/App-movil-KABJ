package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.data.dto.OperationResult
import pe.kabj.app_movil_kabj.data.dto.OperationResult.Success
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderGeneralResponse
import pe.kabj.app_movil_kabj.databinding.FragmentWorkOrderDetailBinding
import pe.kabj.app_movil_kabj.presentation.enums.WorkOrderState
import pe.kabj.app_movil_kabj.presentation.utils.ModalDialogUtils
import pe.kabj.app_movil_kabj.presentation.viewmodels.WorkOrderDetailViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.getValue

class WorkOrderDetailFragment : Fragment() {

    private var _binding: FragmentWorkOrderDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkOrderDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkOrderDetailViewModel(requireContext(), workOrder) as T
            }
        }
    }

    private lateinit var workOrder :WorkOrderGeneralResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        workOrder = arguments?.getParcelable<WorkOrderGeneralResponse>("workOrder")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupComboBoxState()
        initData()
        setupListeners()
        setupObservers()
    }

    private fun setupComboBoxState() {
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, WorkOrderState.entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerState.adapter = adapter

        if (!viewModel.userHasPermissionChangeState()) {
            binding.spinnerState.isEnabled = false
            binding.spinnerState.isClickable = false
            binding.spinnerState.alpha = 0.5f
        }

    }

    private fun initData() {
        binding.tvWorkOrderNumber.text = workOrder.numberWorkOrder.toString()
        binding.spinnerState.setSelection(WorkOrderState.entries.indexOf(workOrder.state))
        binding.tvWorkOrderActivity.text = workOrder.activity.description
        binding.tvWorkOrderSubActivity.text = workOrder.subActivity.description.toString()
        binding.tvWorkOrderForeman.text = workOrder.employeeNameResponse?.getFullName() ?: "- - CAPATAZ NO ASIGNADO - -"
        binding.tvWorkOrderType.text = workOrder.workOrderType
        binding.tvWorkOrderCost.text = workOrder.totalPrice.toString()
        binding.tvWorkOrderDescription.text = workOrder.description
        binding.tvWorkOrderObservation.text = workOrder.observation
        binding.tvWorkOrderDistrict.text = workOrder.supplyNumber.district
        binding.tvWorkOrderSector.text = workOrder.supplyNumber.sector.toString()
        binding.tvWorkOrderLocality.text = workOrder.supplyNumber.locality
        binding.tvWorkOrderAddress.text = workOrder.supplyNumber.address
        // == Dates ==
        binding.tvWorkOrderStartDate.text = formatIsoDateString(workOrder.startDate)
        binding.tvWorkOrderWorkingDate.text = formatIsoDateString(workOrder.workingDate)
        binding.tvWorkOrderEstimatedEnd.text = formatIsoDateString(workOrder.finalEstimateDate)
        binding.tvWorkOrderAttendedDate.text = formatIsoDateString(workOrder.attentionDate)
        binding.tvWorkOrderInvoicedDate.text = formatIsoDateString(workOrder.billingDate)
        binding.tvWorkOrderCompletedDate.text = formatIsoDateString(workOrder.completedDate)
        binding.tvWorkOrderScheduledDate.text = formatIsoDateString(workOrder.scheduledDate)
        binding.tvWorkOrderResolvedDate.text = formatIsoDateString(workOrder.resultDate)
        binding.tvWorkOrderPendingDate.text = formatIsoDateString(workOrder.pendingDate)
        binding.tvWorkOrderRevisedDate.text = formatIsoDateString(workOrder.revisedDate)
        binding.tvWorkOrderCanceledDate.text = formatIsoDateString(workOrder.cancellationDate)
    }

    private fun setupListeners() {
        binding.btnReturn.setOnClickListener {
            handleBackPress()
        }

        if (viewModel.userHasPermissionChangeState()) {
            binding.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedState = parent.getItemAtPosition(position) as WorkOrderState
                    viewModel.selectNewState(selectedState)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        binding.btnSaveWorkOrder.setOnClickListener {
            val newState = WorkOrderState.entries.firstOrNull { it.name == binding.spinnerState.selectedItem.toString() }

            if (newState != null) {
                viewModel.saveChanges()
            } else {
                Toast.makeText(requireContext(), "Seleccione un estado válido", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setupObservers() {
        viewModel.hasChange.observe(viewLifecycleOwner) { hasChanges ->
            if (hasChanges) {
                binding.btnSaveWorkOrder.isEnabled = true
                binding.btnSaveWorkOrder.visibility = View.VISIBLE
            } else {
                binding.btnSaveWorkOrder.isEnabled = false
                binding.btnSaveWorkOrder.visibility = View.GONE
            }
        }
        viewModel.messageResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is OperationResult.Error -> {
                    ModalDialogUtils.showFailureDialog(requireContext(), result.title, result.message)
                }
                is Success -> {
                    ModalDialogUtils.showSuccessDialog(requireContext(), result.title!!, result.message!!)
                }
            }
        }
    }

    private fun handleBackPress() {

        if(viewModel.hasChange.value == true){
            ModalDialogUtils.showConfirmDialog(requireContext(), "¿Seguro que deseas salir sin guardar los cambios?") {
                parentFragmentManager.popBackStack()
            }
        } else {
            parentFragmentManager.popBackStack()
        }

    }

    private fun formatIsoDateString(isoDateString: String): String {
        val formatterOutput = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val dateTime = LocalDateTime.parse(isoDateString)
        return dateTime.format(formatterOutput)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
