package pe.kabj.app_movil_kabj.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.kabj.app_movil_kabj.databinding.ItemWorkOrderAssignBinding
import pe.kabj.app_movil_kabj.model.WorkOrderListItem

class WorkOrderAdapter : RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder>() {

    private val workOrders = mutableListOf<WorkOrderListItem>()

    fun submitList(newList: List<WorkOrderListItem>) {
        workOrders.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkOrderViewHolder {
        val binding = ItemWorkOrderAssignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int) {
        holder.bind(workOrders[position])
    }

    override fun getItemCount(): Int = workOrders.size

    inner class WorkOrderViewHolder(private val binding: ItemWorkOrderAssignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkOrderListItem) {
            binding.tvWorkOrderNumber.text = item.numberWorkOrder.toString()
            binding.tvActivityDescription.text = item.activityDescription
            binding.tvState.text = item.state
            binding.tvStartDate.text = item.startDate.toString()
        }
    }
}
