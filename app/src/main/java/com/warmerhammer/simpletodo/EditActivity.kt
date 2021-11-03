package com.warmerhammer.simpletodo

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        val editText = intent.getStringExtra("EditText")

        Objects.requireNonNull(supportActionBar!!.setTitle("Edit Item"))

        val etItem = findViewById<TextView>(R.id.etItem)
        val btnSave = findViewById<Button>(R.id.btnSave)

        etItem.text = editText

        // when user is done editing, they click the save button
        btnSave.setOnClickListener {
            val intent = Intent()
            intent.putExtra("EditText", etItem.text.toString())
            intent.putExtra("Position", intent.getIntExtra("Position", 0))
            intent.putExtra("Date", intent.getStringExtra("Date"))
            setResult(RESULT_OK, intent)
            onSubmit()
        }
    }

    fun onSubmit() {
        // finish activity, close the screen and go back
        this.finish()
    }

}