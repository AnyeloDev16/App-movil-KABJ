package pe.kabj.app_movil_kabj.presentation.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.databinding.ModalAlertBinding
import pe.kabj.app_movil_kabj.databinding.ModalLoadingBinding

object ModalDialogUtils {

    fun showProgressDialog(context: Context, message: String) {

        val binding = ModalLoadingBinding.inflate(LayoutInflater.from(context))

        binding.tvMessage.text = message

        MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(binding.root)
            .show()

    }

    fun showSuccessDialog(context: Context, title: String, message: String) {

        val binding = ModalAlertBinding.inflate(LayoutInflater.from(context))

        binding.ivIcon.setImageResource(R.drawable.ic_success)
        binding.tvTitle.text = title
        binding.tvMessage.text = message

        binding.btnCancel.visibility = View.GONE
        binding.btnAccept.setBackgroundColor(context.getColor(R.color.btn_success))

        val dialog : AlertDialog = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnAccept.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnKeyListener { _, keyCode, _ ->
            keyCode == android.view.KeyEvent.KEYCODE_BACK
        }

        dialog.show()

    }

    fun showFailureDialog(context: Context, title: String, message: String) {

        val binding = ModalAlertBinding.inflate(LayoutInflater.from(context))

        binding.ivIcon.setImageResource(R.drawable.ic_failure)
        binding.tvTitle.text = title
        binding.tvMessage.text = message

        binding.btnCancel.visibility = View.GONE
        binding.btnAccept.setBackgroundColor(context.getColor(R.color.btn_failure))

        val dialog : AlertDialog = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnAccept.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnKeyListener { _, keyCode, _ ->
            keyCode == android.view.KeyEvent.KEYCODE_BACK
        }

        dialog.show()

    }

    fun showConfirmDialog(context: Context, title: String, onAccept: () -> Unit) {

        val binding = ModalAlertBinding.inflate(LayoutInflater.from(context))

        binding.ivIcon.setImageResource(R.drawable.ic_question)
        binding.tvTitle.text = title

        binding.tvMessage.visibility = View.GONE

        binding.btnAccept.setBackgroundColor(context.getColor(R.color.btn_question))

        val dialog : AlertDialog = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnAccept.setOnClickListener {
            onAccept.invoke()
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnKeyListener { _, keyCode, _ ->
            keyCode == android.view.KeyEvent.KEYCODE_BACK
        }

        dialog.show()

    }

}