package pe.kabj.app_movil_kabj.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.presentation.viewmodels.HomeViewModel
import pe.kabj.app_movil_kabj.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding =  FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sessionManager = SessionManager(requireContext())

        setupObservers()

        viewModel.loadVisibleCards(sessionManager.fetchPermissions())
    }

    private fun setupObservers(){
        viewModel.visibleCards.observe(viewLifecycleOwner) { visibleCards ->
            binding.cardRegisterWorkOrder.visibility =
                if (visibleCards.contains("REGISTER")) View.VISIBLE else View.GONE
            binding.cardConsultWorkOrder.visibility =
                if (visibleCards.contains("CONSULT")) View.VISIBLE else View.GONE
            binding.cardAssignWorkOrder.visibility =
                if (visibleCards.contains("ASSIGN")) View.VISIBLE else View.GONE
            binding.cardForemanWorkOrders.visibility =
                if (visibleCards.contains("FOREMAN")) View.VISIBLE else View.GONE
        }

    }

}