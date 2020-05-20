package com.zulfahmi.todolist.activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zulfahmi.todolist.R
import com.zulfahmi.todolist.adapter.TodoAdapter
import com.zulfahmi.todolist.model.Todo
import com.zulfahmi.todolist.util.Commons
import com.zulfahmi.todolist.util.FormDialog
import com.zulfahmi.todolist.viewmodel.TodoViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlinx.android.synthetic.main.fragment_todo.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoAdapter

    private val todoList: ArrayList<Todo> = ArrayList<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val dataNote = arrayOf("Ini adalah aktivitas saya", "todo 2", "todo 3", "todo 4")
        val dataDate = arrayOf("01/01/2020", "02/01/2020", "03/01/2020", "04/01/2020")
        dataNote.forEachIndexed { index, item ->
            val todo = Todo(
                title = item,
                dateCreated = dataDate[index],
                dateUpdated = dataDate[index],
                note = item,
                dueDate = dataDate[index]
            )

            todoList.add(todo)
        }

        todoAdapter = TodoAdapter(todoList){

        }

        val layoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = layoutManager

        recyclerview.adapter = todoAdapter
        todoAdapter.notifyDataSetChanged()

        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)
        todoViewModel.getTodos()?.observe(this, Observer {

        })


        fab.setOnClickListener { view ->
            val view = LayoutInflater.from(this).inflate(R.layout.fragment_todo, null)

            view.input_due_date.setOnClickListener {
                Commons.showDatePickerDialog(this, view.input_due_date)
            }

            view.input_time.setOnClickListener {
                Commons.showTimePickerDialog(this, view.input_time)
            }

            val dialogTitle = "Add data"
            val toastMessage = "Data has been added successfully"
            val failAlertMessage = "Please fill all the required fields"

            FormDialog(this, dialogTitle, view){
                val title = view.input_title.text.toString().trim()
                val date = view.input_due_date.text.toString().trim()
                val time = view.input_time.text.toString().trim()

                if (title == "" || date == "" || time == "") {
                    AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                        .setPositiveButton("OK") { dialogInterface, _ ->
                            dialogInterface.cancel()
                        }.create().show()
                } else {

//                    val newItem = database.push()

                    val dueDate = "$date, $time"
                    val note = view.input_note.text.toString()
                    val dateCreated = Commons.getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss")
                    val remindMe = true

                    val todo = Todo(
                        title = title,
                        note = note,
                        dateCreated = dateCreated,
                        dateUpdated = dateCreated,
                        dueDate = dueDate,
                        remindMe = remindMe
                    )

//                    newItem.setValue(pertemuan)
                    Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
                }
            }.show()
        }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
