package com.warmerhammer.simpletodo

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity(), DatePickerFragment.OnDateSetListener {

    var listOfTasks = mutableListOf<Task>()
    var listOfTasksIndex: Int? = null
    private lateinit var userInputTask: String
    lateinit var adapter: TaskItemAdapter
    val REQUEST_CODE = 20

    private val editActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val itemText = intent?.getStringExtra("EditText")
                val position = intent?.getIntExtra("Position", 0)
                listOfTasks[position!!].title = itemText!!
                adapter.notifyItemChanged(position)

                //persist changes
                saveItems()

                Toast.makeText(this, "Item updated successfully", Toast.LENGTH_LONG).show()
            } else {
                Log.w("MainActivity", "Unknown call to onActivityResult")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onLongClickListener = object : TaskItemAdapter.OnLongClickListener {
            override fun onItemLongClicked(position: Int) {
                // 1. Remove item from list
                listOfTasks.removeAt(position)

                // 2. Notify the adapter that data set has changed
                adapter.notifyDataSetChanged()

                // 3. save items to file
                saveItems()

                Toast.makeText(this@MainActivity, "Item successfully deleted", Toast.LENGTH_LONG)
                    .show()

                val noTasksToDisplay = findViewById<TextView>(R.id.no_tasks_textview)

                if (listOfTasks.isEmpty()) {
                    findViewById<RecyclerView>(R.id.recyclerView).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.taskListHeader).visibility = View.GONE
                    noTasksToDisplay.visibility = View.VISIBLE
                }
            }
        }

        val onClickListener = object : TaskItemAdapter.OnClickListener {
            override fun onItemClicked(position: Int) {
                // 1. Create new activity
                val i = Intent(this@MainActivity, EditActivity::class.java)

                // pass data to be edited
                i.putExtra("EditText", listOfTasks[position].title)
                i.putExtra("Date", listOfTasks[position].date)
                i.putExtra("Position", position)
                i.putExtra("Code", REQUEST_CODE)

                // launch EditActivity
                editActivityLauncher.launch(i)
            }
        }

        val onDateClickListener = object : TaskItemAdapter.OnDateClickListener {
            override fun onDateClicked(position: Int) {
                userInputTask = listOfTasks[position].title
                listOfTasksIndex = position
                val datePicker = DatePickerFragment()
                datePicker.show(supportFragmentManager, "datePicker")
            }
        }

        val onPrioritySelectedListener = object : TaskItemAdapter.OnPriorityItemSelectedListener {
            override fun onPrioritySelected(taskIdx: Int, priorityNumber: Int) {

                if (listOfTasks[taskIdx].priority != priorityNumber) {
                    Log.i("MainActivity", "taskIdx: $taskIdx, priorityNumber: $priorityNumber")
                    listOfTasks[taskIdx].priority = priorityNumber

                    listOfTasks.sortWith { taskOne, taskTwo ->
                        if (taskOne.priority < taskTwo.priority) -1 else 1
                    }
                    adapter.notifyDataSetChanged()

                    saveItems()
                }
                }

        }

        loadItems()

        // Lookup recyclerView in the layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val noTasksToDisplay = findViewById<TextView>(R.id.no_tasks_textview)
        // create adapter passing in the sample user data
        adapter =
            TaskItemAdapter(
                this,
                listOfTasks,
                onLongClickListener,
                onClickListener,
                onDateClickListener,
                onPrioritySelectedListener
            )
        // attach adapter to the recyclerView
        recyclerView.adapter = adapter
        // set layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (listOfTasks.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.taskListHeader).visibility = View.VISIBLE
            noTasksToDisplay.visibility = View.GONE
        } else {
            noTasksToDisplay.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            findViewById<LinearLayout>(R.id.taskListHeader).visibility = View.GONE
        }

//        // set up spinner
//        val spinner = SpinnerAdapter(this, null, onPrioritySelectedListener)

        // Set up button and the input field, so the user can enter a task and add it to the list
        val inputTextField = findViewById<EditText>(R.id.addTaskField)

        findViewById<Button>(R.id.button).setOnClickListener {
            // 1. Grab text that the user has input into the edit text field @id/addTaskField
            userInputTask = inputTextField.text.toString()

            if (userInputTask.isBlank()) {
                Toast.makeText(this@MainActivity, "Please enter valid task.", Toast.LENGTH_LONG)
                    .show()
            } else {
                // 2. Open the date picker
                val datePicker = DatePickerFragment()
                datePicker.show(supportFragmentManager, "datePicker")
            }


            // 3. Reset text field
            inputTextField.setText("")

        }

        // check to see if list of tasks is not empty
        // otherwise display message that there are no tasks at present
        if (listOfTasks.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            noTasksToDisplay.visibility = View.GONE
        } else {
            noTasksToDisplay.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    override fun onDateSet(date: String?) {
        val newDate = date ?: "TBD"
        // 2. Add new task to listOfTasks
        if (listOfTasksIndex == null) {
            listOfTasks.add(Task(userInputTask, newDate))
            // Notify adapter that the data list has changed
            adapter.notifyItemInserted(listOfTasks.size - 1)
        } else {
            listOfTasks[listOfTasksIndex!!].date = date!!
            // Notify adapter that data list has changed at specified index
            adapter.notifyItemChanged(listOfTasksIndex!!)
            listOfTasksIndex = null
        }

        findViewById<RecyclerView>(R.id.recyclerView).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.taskListHeader).visibility = View.VISIBLE
        findViewById<TextView>(R.id.no_tasks_textview).visibility = View.GONE

        // 3. Save items to stored file
        saveItems()
    }

    // Save the data that the user has input
    // Save data by writing and reading from a file

    // Load items by reading every line in our file
    private fun loadItems() {
        try {
            openFileInput("data.txt").bufferedReader().useLines { lines ->
                lines.forEach {
                    val splitLine = it.split(",")

                    listOfTasks.add(Task(splitLine[0], splitLine[1], splitLine[2].trim().toInt()))
                }
            }

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    // Save items by writing them into our data file
    fun saveItems() {
        try {

            openFileOutput("data.txt", Context.MODE_PRIVATE).use {
                for (task in listOfTasks) {
                    it.write("${task.title}, ${task.date}, ${task.priority}\n".toByteArray())
                }
            }

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

}