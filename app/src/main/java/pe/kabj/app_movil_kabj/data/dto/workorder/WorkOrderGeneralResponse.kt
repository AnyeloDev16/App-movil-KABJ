package pe.kabj.app_movil_kabj.data.dto.workorder

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import pe.kabj.app_movil_kabj.data.dto.activity.ActivityResponse
import pe.kabj.app_movil_kabj.data.dto.employee.EmployeeNamesResponse
import pe.kabj.app_movil_kabj.data.dto.supplynumber.SupplyNumberResponse
import kotlinx.parcelize.Parcelize
import pe.kabj.app_movil_kabj.data.dto.activity.SubActivityResponse
import pe.kabj.app_movil_kabj.presentation.enums.WorkOrderState

@Parcelize
data class WorkOrderGeneralResponse(

    @SerializedName("id_work_order")
    val idWorkOrder: Long,

    @SerializedName("number_work_order")
    val numberWorkOrder: Long,

    @SerializedName("supply_number")
    val supplyNumber: SupplyNumberResponse,

    @SerializedName("activity")
    val activity: ActivityResponse,

    @SerializedName("sub_activity")
    val subActivity: SubActivityResponse,

    @SerializedName("employee")
    val employeeNameResponse: EmployeeNamesResponse?,

    @SerializedName("work_order_type")
    val workOrderType: String,

    @SerializedName("state")
    val state: WorkOrderState,

    @SerializedName("description")
    val description: String,

    @SerializedName("observation")
    val observation: String,

    @SerializedName("total_price")
    val totalPrice: Double,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("final_estimate_date")
    val finalEstimateDate: String,

    @SerializedName("billing_date")
    val billingDate: String,

    @SerializedName("scheduled_date")
    val scheduledDate: String,

    @SerializedName("pending_date")
    val pendingDate: String,

    @SerializedName("working_date")
    val workingDate: String,

    @SerializedName("attention_date")
    val attentionDate: String,

    @SerializedName("completed_date")
    val completedDate: String,

    @SerializedName("result_date")
    val resultDate: String,

    @SerializedName("revised_date")
    val revisedDate: String,

    @SerializedName("cancellation_date")
    val cancellationDate: String

) : Parcelable