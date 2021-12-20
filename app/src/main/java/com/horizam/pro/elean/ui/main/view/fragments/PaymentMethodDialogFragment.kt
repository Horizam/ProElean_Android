package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.DialogPaymentMethodBinding
import com.horizam.pro.elean.ui.main.view.activities.StripePaymentActivity

class PaymentMethodDialogFragment : DialogFragment() {

    private lateinit var binding: DialogPaymentMethodBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            binding = DialogPaymentMethodBinding.bind(inflater.inflate(R.layout.dialog_payment_method, null))
            builder.setView(binding.root)
            clicks()
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun clicks() {
        binding.cardViewStripe.setOnClickListener {
            /*val intent = Intent(requireActivity(),StripePaymentActivity::class.java).also {
                startActivity(it)
            }*/
            this.dismiss()
            Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.cardViewPayStack.setOnClickListener {
            this.dismiss()
            Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
        }
    }


}