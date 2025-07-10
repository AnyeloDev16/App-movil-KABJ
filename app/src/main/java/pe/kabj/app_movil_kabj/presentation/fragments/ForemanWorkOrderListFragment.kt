package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.model.dto.OperationResult
import pe.kabj.app_movil_kabj.databinding.FragmentForemanWorkOrderListBinding
import pe.kabj.app_movil_kabj.presentation.adapters.WorkOrderAdapter
import pe.kabj.app_movil_kabj.presentation.utils.ModalDialogUtils
import pe.kabj.app_movil_kabj.presentation.viewmodels.ForemanWorkOrderListViewModel
import pe.kabj.app_movil_kabj.presentation.viewmodels.SharedWorkOrderEventViewModel

class ForemanWorkOrderListFragment : Fragment() {

    private var _binding: FragmentForemanWorkOrderListBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedWorkOrderEventViewModel by activityViewModels()
    private val viewModel: ForemanWorkOrderListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ForemanWorkOrderListViewModel(requireContext()) as T
            }
        }
    }

    private lateinit var workOrderAdapter: WorkOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForemanWorkOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        setupObservers()
        loadActualPage()
    }

    private fun setupRecyclerView() {
        workOrderAdapter = WorkOrderAdapter { numberWorkOrder ->
            viewModel.searchWorkOrderBy(numberWorkOrder)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewWorkOrders.layoutManager = layoutManager
        binding.recyclerViewWorkOrders.adapter = workOrderAdapter

    }

    private fun setupListeners() {
        binding.btnPreviousPage.setOnClickListener {
            viewModel.goToPreviousPage()
        }
        binding.btnNextPage.setOnClickListener {
            viewModel.goToNextPage()
        }
    }

    private fun setupObservers() {
        viewModel.items.observe(viewLifecycleOwner) { listItems ->
            workOrderAdapter.submitList(listItems)
        }
        viewModel.actualPage.observe(viewLifecycleOwner) { actualPage ->
            val actualPageTextByUsers = actualPage + 1
            binding.tvPageActual.text = actualPageTextByUsers.toString()
        }
        viewModel.totalPage.observe(viewLifecycleOwner) { totalPage ->
            val totalPageTextByUsers = if (totalPage == 0L) 1L else totalPage
            binding.tvPageTotal.text = totalPageTextByUsers.toString()
        }
        viewModel.btnEnableBtnPreviousPage.observe(viewLifecycleOwner) { isEnable ->
            binding.btnPreviousPage.isEnabled = isEnable
        }
        viewModel.btnEnableBtnNextPage.observe(viewLifecycleOwner) { isEnable ->
            binding.btnNextPage.isEnabled = isEnable
        }
        viewModel.messageResult.observe(viewLifecycleOwner) { result ->
            if (result is OperationResult.Error) {
                ModalDialogUtils.showFailureDialog(requireContext(), result.title, result.message)
            }
        }
        viewModel.workOrder.observe(viewLifecycleOwner) { workOrder ->
            if (workOrder != null) {

                val fragment = WorkOrderDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("workOrder", workOrder)
                    }

                }

                parentFragmentManager.beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack("WorkOrderDetailForeman")
                    .commit()
            } else {
                ModalDialogUtils.showFailureDialog(
                    requireContext(),
                    "Ordern no encontrada",
                    "No se encontró la orden de trabajo a la que intenta acceder."
                )
            }

        }
        sharedViewModel.shouldRefreshList.observe(viewLifecycleOwner) { shouldRefresh ->
            if (shouldRefresh) {
                viewModel.gotToActualPage()
                sharedViewModel.resetRefreshFlag()
            }
        }
    }

    private fun loadActualPage() {
        viewModel.gotToActualPage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
