package com.warmerhammer.simpletodo

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.spinner_item.view.*
import kotlinx.android.synthetic.main.todo_item.*

/**
 * A bridge that tells the recycler view how to display the data we give it
 */
class TaskItemAdapter(
    private val context: Context,
    private val listOfItems: List<Task>,
    val longClickListener: OnLongClickListener,
    val onClickListener: OnClickListener,
    val onDateClickListener: OnDateClickListener,
    val onPriorityIemSelected: OnPriorityItemSelectedListener
) : RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onItemClicked(position: Int)
    }

    interface OnLongClickListener {
        fun onItemLongClicked(position: Int)
    }

    interface OnDateClickListener {
        fun onDateClicked(position: Int)
    }

    interface OnPriorityItemSelectedListener {
        fun onPrioritySelected(adapterPos: Int, itemPos: Int)
    }

    var userTouch = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView =
            inflater.inflate(R.layout.todo_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: TaskItemAdapter.ViewHolder, position: Int) {
        //Get the data model based on the position
        val item = listOfItems[position]
        holder.textView.text = item.title
        holder.dateView.text = item.date
        holder.priorityView.setSelection(item.priority)
    }

    override fun getItemCount(): Int = listOfItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Store references to elements in our layout view
        val textView: TextView = itemView.findViewById(R.id.todo_item_textview)
        val dateView: TextView = itemView.findViewById(R.id.todo_item_dateview)
        val priorityView: Spinner = itemView.findViewById(R.id.priority_spinner)

        init {
            itemView.setOnLongClickListener {
                longClickListener.onItemLongClicked(adapterPosition)
                true
            }
            itemView.setOnClickListener {
                onClickListener.onItemClicked(adapterPosition)
            }
            dateView.setOnClickListener {
                onDateClickListener.onDateClicked(adapterPosition)
            }

            // create instance of custom spinner adapter and listener
            val adapter = SpinnerAdapter(context, listOf("High", "Medium", "Low"))
            priorityView.adapter = adapter

            priorityView.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    Log.i("TaskItemAdapter", "$userTouch")
                        Log.i("TaskItemAdapter", "$adapterPosition")
                        onPriorityIemSelected.onPrioritySelected(adapterPosition, pos)
                        userTouch = false
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

        }
    }


}