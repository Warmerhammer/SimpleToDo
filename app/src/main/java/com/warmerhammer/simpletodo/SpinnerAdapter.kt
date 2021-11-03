package com.warmerhammer.simpletodo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.spinner_item.view.*

class SpinnerAdapter(context: Context, taskList: List<String>) :
    ArrayAdapter<String>(context, 0, taskList), AdapterView.OnItemSelectedListener {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val task = getItem(position)
        val view =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        view.spinnerTextView.text = task
        return view
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}