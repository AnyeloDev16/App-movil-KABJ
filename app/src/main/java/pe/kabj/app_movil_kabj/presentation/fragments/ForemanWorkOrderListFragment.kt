package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.databinding.FragmentForemanWorkOrderListBinding
import pe.kabj.app_movil_kabj.presentation.adapters.WorkOrderAdapter
import pe.kabj.app_movil_kabj.presentation.viewmodels.ForemanWorkOrderListViewModel
import pe.kabj.app_movil_kabj.presentation.utils.PaginationScrollListener

class ForemanWorkOrderListFragment : Fragment() {

    private var _binding: FragmentForemanWorkOrderListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForemanWorkOrderListViewModel by viewModels()

    private lateinit var workOrderAdapter: WorkOrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForemanWorkOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        viewModel.loadFirstPage()
    }

    private fun setupRecyclerView() {
        workOrderAdapter = WorkOrderAdapter { id ->
            // Buscar por ID los datos completos de la orden de trabajo

            parentFragmentManager.beginTransaction()
                .hide(this)
                .add(R.id.fragment_container, WorkOrderDetailForemanFragment())
                .addToBackStack("WorkOrderDetailForeman")
                .commit()
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewWorkOrders.layoutManager = layoutManager
        binding.recyclerViewWorkOrders.adapter = workOrderAdapter

        binding.recyclerViewWorkOrders.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                viewModel.loadNextPage()
            }

            override fun isLastPage(): Boolean {
                return viewModel.isLastPage
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading
            }
        })
    }

    private fun setupObservers() {
        viewModel.workOrders.observe(viewLifecycleOwner) { workOrders ->
            workOrderAdapter.submitList(workOrders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
