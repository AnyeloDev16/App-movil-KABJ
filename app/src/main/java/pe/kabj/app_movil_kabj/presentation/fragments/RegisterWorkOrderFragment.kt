package pe.kabj.app_movil_kabj.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.databinding.FragmentRegisterWorkOrderBinding
import pe.kabj.app_movil_kabj.presentation.enums.ValidationState
import pe.kabj.app_movil_kabj.presentation.viewmodels.RegisterWorkOrderViewModel

class RegisterWorkOrderFragment : Fragment() {

    private var _binding: FragmentRegisterWorkOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterWorkOrderViewModel by viewModels()
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    viewModel.setSelectedFile(it, requireContext())
                    binding.cardFileInfo.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Archivo cargado correctamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterWorkOrderBinding.inflate(inflater, container, false)

        // Escucha cambios de validación
        viewModel.validationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ValidationState.VALIDATING -> {
                    binding.tvValidationTime.text = "Validando..."
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressValidation.visibility = View.VISIBLE
                    binding.ivStatusIcon.visibility = View.GONE
                    binding.btnProcessFile.isEnabled = false
                }
                ValidationState.SUCCESS -> {
                    binding.tvValidationTime.text = "Validación exitosa"
                    binding.ivStatusIcon.setImageResource(R.drawable.succes)
                    binding.ivStatusIcon.visibility = View.VISIBLE
                    binding.progressValidation.visibility = View.GONE
                    binding.btnProcessFile.isEnabled = true
                }
                ValidationState.ERROR -> {
                    binding.tvValidationTime.text = "Validación fallida"
                    binding.ivStatusIcon.setImageResource(R.drawable.error)
                    binding.ivStatusIcon.visibility = View.VISIBLE
                    binding.progressValidation.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.btnProcessFile.isEnabled = false
                }
                ValidationState.NONE -> {
                    binding.tvValidationTime.text = ""
                    binding.ivStatusIcon.visibility = View.GONE
                    binding.progressValidation.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.btnProcessFile.isEnabled = false
                }
            }
        }

        viewModel.selectedFileName.observe(viewLifecycleOwner) {
            binding.tvFileName.text = it
        }

        viewModel.fileSizeFormatted.observe(viewLifecycleOwner) {
            binding.tvFileSize.text = it
        }

        viewModel.progressPercent.observe(viewLifecycleOwner) { progress ->
            binding.progressBar.progress = progress
        }

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
            val success = viewModel.processSelectedFile(requireContext())
            if (success) {
                Toast.makeText(requireContext(), "Archivo procesado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No hay archivo válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Eliminar archivo
        binding.btnRemoveFile.setOnClickListener {
            viewModel.clearFileSelection()
            binding.cardFileInfo.visibility = View.GONE
            Toast.makeText(requireContext(), "Archivo eliminado", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
