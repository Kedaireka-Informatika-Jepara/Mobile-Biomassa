package com.cemebsa.biomassa.views.components

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment() {

    companion object {
        fun create(): TimePickerFragment = TimePickerFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            parentFragment as TimePickerDialog.OnTimeSetListener,
            hour,
            minute,
            true
        )
    }
}