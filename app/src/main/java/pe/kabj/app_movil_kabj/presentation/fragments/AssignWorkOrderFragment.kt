package pe.kabj.app_movil_kabj.presentation.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.presentation.viewmodels.AssignWorkOrderViewModel
import pe.kabj.app_movil_kabj.databinding.FragmentAssignWorkOrderBinding
import pe.kabj.app_movil_kabj.presentation.enums.ValidationState

class AssignWorkOrderFragment : Fragment() {

    private var _binding: FragmentAssignWorkOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignWorkOrderViewModel by viewModels()

    private lateinit var layoutForemanSelector : TextInputLayout
    private lateinit var autoCompleteForeman : MaterialAutoCompleteTextView
    private lateinit var cardDropZone : CardView
    private lateinit var btnChooseFile : TextView
    private lateinit var cardFileInfo : CardView
    private lateinit var btnRemoveFile : MaterialButton
    private lateinit var btnProcessFile : MaterialButton

    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                viewModel.setSelectedFile(it, requireContext())
                Toast.makeText(requireContext(), "Archivo cargado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignWorkOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupComponents()
        setupListeners()
        setupObservers()
    }

    private fun setupComponents() {
        layoutForemanSelector = binding.layoutForemanSelector
        autoCompleteForeman = binding.autoCompleteForeman
        cardDropZone = binding.cardDropZone
        btnChooseFile = binding.btnChooseFile
        cardFileInfo = binding.cardFileInfo
        btnRemoveFile = binding.btnRemoveFile
        btnProcessFile = binding.btnProcessFile
    }

    private fun setupListeners() {
        btnChooseFile.setOnClickListener {
            openFilePicker()
        }
        btnRemoveFile.setOnClickListener {
            viewModel.clearFileSelection()
            Toast.makeText(requireContext(), "Archivo eliminado", Toast.LENGTH_SHORT).show()
        }
        btnProcessFile.setOnClickListener {
            viewModel.selectedFileName.value
        }
    }

    private fun setupObservers() {
        viewModel.isVisibleFileInfo.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) {
                cardFileInfo.visibility = View.VISIBLE
            } else {
                cardFileInfo.visibility = View.GONE
            }

        }
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
        viewModel.selectedFileName.observe(viewLifecycleOwner) { fileName ->
            binding.tvFileName.text = fileName
        }
        viewModel.fileSizeFormatted.observe(viewLifecycleOwner) { fileSize ->
            binding.tvFileSize.text = fileSize
        }
        viewModel.progressPercent.observe(viewLifecycleOwner) { progress ->
            binding.progressBar.progress = progress
        }
    }

    private fun openFilePicker() {
        filePickerLauncher.launch(arrayOf(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ))
    }

}