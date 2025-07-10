package pe.kabj.app_movil_kabj.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.model.dto.OperationResult
import pe.kabj.app_movil_kabj.model.dto.OperationResult.Success
import pe.kabj.app_movil_kabj.model.dto.employee.EmployeeForemanResponse
import pe.kabj.app_movil_kabj.presentation.viewmodels.AssignWorkOrderViewModel
import pe.kabj.app_movil_kabj.databinding.FragmentAssignWorkOrderBinding
import pe.kabj.app_movil_kabj.presentation.enums.ValidationState
import pe.kabj.app_movil_kabj.presentation.utils.ModalDialogUtils

class AssignWorkOrderFragment : Fragment() {

    private var _binding: FragmentAssignWorkOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignWorkOrderViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AssignWorkOrderViewModel(requireContext()) as T
            }
        }
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let { safeUri ->
                context?.let { ctx ->
                    viewModel.uploadUri(safeUri)
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignWorkOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getAllForemanActive()
        setupListeners()
        setupObservers()
    }

    private fun setupComboBoxState(allForemanActive: List<EmployeeForemanResponse>) {
        val adapter = object : ArrayAdapter<EmployeeForemanResponse>(
            requireContext(),
            R.layout.spinner_item,
            allForemanActive
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).text = "${getItem(position)?.names} ${getItem(position)?.surnames}"
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).text = "${getItem(position)?.names} ${getItem(position)?.surnames}"
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerState.adapter = adapter

        if (!viewModel.userHasPermissionAssignForeman()) {
            binding.spinnerState.isEnabled = false
            binding.spinnerState.isClickable = false
            binding.spinnerState.alpha = 0.5f
        }
    }

    private fun setupListeners() {

        binding.btnChooseFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            filePickerLauncher.launch(Intent.createChooser(intent, "Selecciona archivo Excel"))
        }

        binding.btnRemoveFile.setOnClickListener {

            ModalDialogUtils.showConfirmDialog(requireContext(), "¿Seguro que deseas eliminar el Archivo subido?") {
                viewModel.clearFileUpload()
                Toast.makeText(requireContext(), "Archivo eliminado", Toast.LENGTH_SHORT).show()
            }
        }

        // Procesar archivo
        binding.btnProcessFile.setOnClickListener {
            val selectedForeman = binding.spinnerState.selectedItem as? EmployeeForemanResponse

            if(selectedForeman == null || selectedForeman.idEmployee <= 0){
                ModalDialogUtils.showFailureDialog(requireContext(), "Error", "Selecciona un Supervisor de la lista.")
                return@setOnClickListener
            }

            viewModel.processSelectedFile(selectedForeman)
        }

    }

    private fun setupObservers() {

        viewModel.btnProcessFileEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnProcessFile.isEnabled = isEnabled
        }

        viewModel.fileName.observe(viewLifecycleOwner) { fileName ->
            binding.tvFileName.text = fileName
        }

        viewModel.fileSize.observe(viewLifecycleOwner) { fileSize ->
            binding.tvFileSize.text = fileSize
        }

        viewModel.isValidating.observe(viewLifecycleOwner) { state ->

            when(state){
                ValidationState.NONE -> {
                    binding.cardFileInfo.isVisible = false
                    binding.tvValidationInfo.text = ""
                    binding.ivStatusIcon.visibility = View.GONE
                    binding.progressValidation.visibility = View.GONE
                    binding.btnRemoveFile.visibility = View.GONE
                }
                ValidationState.VALIDATING -> {
                    binding.tvValidationInfo.text = "Validando..."
                    binding.ivStatusIcon.visibility = View.GONE
                    binding.progressValidation.visibility = View.VISIBLE
                    binding.btnRemoveFile.isEnabled = false
                    binding.btnRemoveFile.visibility = View.VISIBLE
                    binding.cardFileInfo.isVisible = true
                }
                ValidationState.SUCCESS -> {
                    binding.tvValidationInfo.text = "Validación exitosa"
                    binding.progressValidation.visibility = View.GONE
                    binding.ivStatusIcon.setImageResource(R.drawable.succes)
                    binding.ivStatusIcon.visibility = View.VISIBLE
                    binding.btnRemoveFile.isEnabled = true
                    binding.btnRemoveFile.visibility = View.VISIBLE
                    binding.cardFileInfo.isVisible = true
                }
                ValidationState.ERROR -> {
                    binding.tvValidationInfo.text = "Validación fallida"
                    binding.progressValidation.visibility = View.GONE
                    binding.ivStatusIcon.setImageResource(R.drawable.error)
                    binding.ivStatusIcon.visibility = View.VISIBLE
                    binding.btnRemoveFile.isEnabled = true
                    binding.cardFileInfo.isVisible = true
                }
            }

        }

        viewModel.foremanList.observe(viewLifecycleOwner) { listAllForeman ->
            setupComboBoxState(listAllForeman)
        }

        viewModel.messageResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is OperationResult.Error -> {
                    showMessageError(result)
                }
                is Success -> {
                    showMessageSuccess(result)
                }
            }
        }
    }

    private fun showMessageError(error: OperationResult.Error) {
        ModalDialogUtils.showFailureDialog(requireContext(), error.title, error.message)
    }

    private fun showMessageSuccess(success: Success) {
        ModalDialogUtils.showSuccessDialog(requireContext(), success.title!!, success.message!!)
    }

}