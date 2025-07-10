package pe.kabj.app_movil_kabj.presentation.enums

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import pe.kabj.app_movil_kabj.R

enum class WorkOrderState(
    @DrawableRes val iconResId: Int,
    @ColorRes val backgroundColorResId: Int
) {
    PENDIENTE(R.drawable.ic_pending, R.color.state_pending),
    TRABAJANDO(R.drawable.ic_working, R.color.state_working),
    FACTURADO(R.drawable.ic_billing, R.color.state_billing),
    RESUELTO(R.drawable.ic_result, R.color.state_result),
    ATENDIDO(R.drawable.ic_attend, R.color.state_attended),
    CONCLUIDO(R.drawable.ic_concluded, R.color.state_concluded),
    ANULADO(R.drawable.ic_canceled, R.color.state_canceled),
    REVISADO(R.drawable.ic_revised, R.color.state_revised)
}
