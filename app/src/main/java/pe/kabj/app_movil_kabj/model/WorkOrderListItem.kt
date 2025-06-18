package pe.kabj.app_movil_kabj.model

import java.time.LocalDate

data class WorkOrderListItem (
    val id: Long,
    val numberWorkOrder: Long,
    val activityDescription: String,
    val state: String,
    val startDate: LocalDate
)
