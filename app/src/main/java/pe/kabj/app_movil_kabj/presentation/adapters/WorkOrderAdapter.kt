package pe.kabj.app_movil_kabj.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.kabj.app_movil_kabj.model.dto.workorder.WorkOrderForemanResponse
import pe.kabj.app_movil_kabj.databinding.ItemWorkOrderAssignBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WorkOrderAdapter(private val onSeeDetailClicked: (Long) -> Unit)
    : RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder>() {

    private val workOrders = mutableListOf<WorkOrderForemanResponse>()

    fun submitList(newList: List<WorkOrderForemanResponse>) {
        workOrders.clear()
        workOrders.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkOrderViewHolder {
        val binding = ItemWorkOrderAssignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkOrderViewHolder(onSeeDetailClicked, binding)
    }

    override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int) {
        holder.bind(workOrders[position])
    }

    override fun getItemCount(): Int = workOrders.size

    inner class WorkOrderViewHolder(private val onSeeDetailClicked: (Long) -> Unit, private val binding: ItemWorkOrderAssignBinding) : RecyclerView.ViewHolder(binding.root) {

        private val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        private val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        @SuppressLint("ResourceAsColor")
        fun bind(item: WorkOrderForemanResponse) {
            binding.tvWorkOrderNumber.text = item.numberWorkOrder.toString()
            binding.tvActivityDescription.text = item.activityDescription
            binding.tvState.text = item.state.name
            binding.ivStateIcon.setImageResource(item.state.iconResId)

            val color = androidx.core.content.ContextCompat.getColor(binding.root.context, item.state.backgroundColorResId)
            binding.ivStateIcon.setBackgroundColor(color)

            // Aseguramos que el string sea parseable como LocalDateTime
            try {
                val date = LocalDateTime.parse(item.startDate, inputFormat)
                binding.tvStartDate.text = date.format(outputFormat)
            } catch (e: Exception) {
                binding.tvStartDate.text = "Fecha inválida"
            }

            binding.btnSeeDetail.setOnClickListener {
                onSeeDetailClicked(item.numberWorkOrder)
            }
        }

    }
}
