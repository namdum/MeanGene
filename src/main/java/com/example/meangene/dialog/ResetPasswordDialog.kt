package com.example.meangene.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.meangene.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setUpBottomSheetDialog(
    onSendClick:(String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val editEmail = view.findViewById<EditText>(R.id.editResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.tvSendResetPasswordButton)
    val buttonCancel = view.findViewById<Button>(R.id.tvCancelResetPasswordButton)

    buttonSend.setOnClickListener{
        val email = editEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()

    }
    buttonCancel.setOnClickListener{
        dialog.dismiss()
    }
}