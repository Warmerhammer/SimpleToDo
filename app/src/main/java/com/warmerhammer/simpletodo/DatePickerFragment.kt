package com.warmerhammer.simpletodo

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*
import kotlin.ClassCastException

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    // Use this instance of the interface to deliver action events
    private lateinit var listener: OnDateSetListener

    interface OnDateSetListener {
        fun onDateSet(date: String?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // use the current date as the default date in the picker
        val c = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH) + 1,
            c.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Skip") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

//        return DatePickerDialog(requireContext(), this, year, month + 1, day)
//            .setButton(DatePickerDialog.BUTTON_NEGATIVE, "Skip", Message())
//            .build()

        return datePickerDialog

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as OnDateSetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement NoticeDialogListener")
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val date = "$month/$day/${year.toString()[2]}${year.toString()[3]}"
        listener.onDateSet(date)
    }


}