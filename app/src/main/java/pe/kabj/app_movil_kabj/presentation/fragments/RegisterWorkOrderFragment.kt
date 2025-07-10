package pe.kabj.app_movil_kabj.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.model.dto.OperationResult.*
import pe.kabj.app_movil_kabj.databinding.FragmentRegisterWorkOrderBinding
import pe.kabj.app_movil_kabj.presentation.enums.ValidationState
import pe.kabj.app_movil_kabj.presentation.utils.ModalDialogUtils
import pe.kabj.app_movil_kabj.presentation.viewmodels.RegisterWorkOrderViewModel

class RegisterWorkOrderFragment : Fragment() {

    private var _binding: FragmentRegisterWorkOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterWorkOrderViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterWorkOrderViewModel(requireContext()) as T
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
        _binding = FragmentRegisterWorkOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners(){

        // Elegir archivo
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

        // Procesar archivo
        binding.btnProcessFile.setOnClickListener {
            viewModel.processSelectedFile()
        }

        // Eliminar archivo
        binding.btnRemoveFile.setOnClickListener {

            ModalDialogUtils.showConfirmDialog(requireContext(), "¿Seguro que deseas eliminar el Archivo subido?") {
                viewModel.clearFileUpload()
                Toast.makeText(requireContext(), "Archivo eliminado", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupObservers(){

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

        viewModel.messageResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Error -> {
                    showMessageError(result)
                }
                is Success -> {
                    showMessageSuccess(result)
                }
            }
        }

    }

    private fun showMessageError(error: Error) {
        ModalDialogUtils.showFailureDialog(requireContext(), error.title, error.message)
    }

    private fun showMessageSuccess(success: Success) {
        ModalDialogUtils.showSuccessDialog(requireContext(), success.title!!, success.message!!)
    }

}
