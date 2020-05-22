package com.zulfahmi.todolist.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zulfahmi.todolist.R
import com.zulfahmi.todolist.adapter.TodoAdapter
import com.zulfahmi.todolist.model.Todo
import com.zulfahmi.todolist.util.Commons
import com.zulfahmi.todolist.util.CustomConfirmDialog
import com.zulfahmi.todolist.util.FormDialog
import com.zulfahmi.todolist.viewmodel.TodoViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_todo.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoAdapter

    private val todoList: ArrayList<Todo> = ArrayList<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = layoutManager

        todoAdapter = TodoAdapter(this){ todo, _ ->
            val options = resources.getStringArray(R.array.option_edit_delete)
            Commons.showSelector(this, "Choose action", options) { _, i ->
                when (i) {
                    0 -> showDetailsDialog(todo)
                    1 -> showEditDialog(todo)
                    2 -> showDeleteDialog(todo)
                }
            }
        }

        recyclerview.adapter = todoAdapter

        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        swipe_refresh_layout.setOnRefreshListener {
            refreshData()
        }

        fab.setOnClickListener {
            showInsertDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        observeData()
    }

    private fun observeData(sortby: String = "dateCreated", keyword: String? = ""){
        todoViewModel.getTodos()?.observe(this, Observer {
            todoList.clear()
            setProgressbarVisibility(false)

            if(it.isEmpty()) setEmptyTextVisibility(true)
            else {
                todoList.addAll(it)
                setEmptyTextVisibility(false)
            }

            todoAdapter.setTodoList(it)
        })

    }

    private fun refreshData(sortby: String = "dateCreated", keyword: String? = ""){
        setProgressbarVisibility(true)
        observeData(sortby, keyword)
        swipe_refresh_layout.isRefreshing = false
        setProgressbarVisibility(false)
    }

    private fun showInsertDialog(){
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
            val note = view.input_note.text.toString()

            val remindMe = true

            if (title == "" || date == "" || time == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                val parsedDate = SimpleDateFormat("dd/MM/yy", Locale.US).parse(date) as Date
                val dueDate = parsedDate.toString("dd MMM yyyy")

                val dateCreated = Commons.getCurrentDateTime().toString("dd MMM yyyy")

                val todo = Todo(
                    title = title,
                    note = note,
                    dateCreated = dateCreated,
                    dateUpdated = dateCreated,
                    dueDate = dueDate,
                    dueTime = time,
                    remindMe = remindMe
                )

                todoViewModel.insertTodo(todo)
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showEditDialog(todo: Todo) {
        val view = LayoutInflater.from(this).inflate(R.layout.fragment_todo, null)

        view.input_due_date.setOnClickListener {
            Commons.showDatePickerDialog(this, view.input_due_date)
        }

        view.input_time.setOnClickListener {
            Commons.showTimePickerDialog(this, view.input_time)
        }

        view.input_title.setText(todo.title)
        view.input_note.setText(todo.note)
        view.input_due_date.setText(todo.dueDate)
        view.input_time.setText(todo.dueTime)

        val dialogTitle = "Edit data"
        val toastMessage = "Data has been updated successfully"
        val failAlertMessage = "Please fill all the required fields"

        FormDialog(this, dialogTitle, view){
            val title = view.input_title.text.toString().trim()
            val date = view.input_due_date.text.toString().trim()
            val time = view.input_time.text.toString().trim()
            val note = view.input_note.text.toString()

            val dateCreated = todo.dateCreated
            val remindMe = true

            if (title == "" || date == "" || time == "") {
                AlertDialog.Builder(this).setMessage(failAlertMessage).setCancelable(false)
                    .setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.create().show()
            } else {
                val parsedDate = SimpleDateFormat("dd/MM/yy", Locale.US).parse(date) as Date
                val dueDate = parsedDate.toString("dd MMM yyyy")

                val dateUpdated = Commons.getCurrentDateTime().toString("dd MMM yyyy")

                todo.title = title
                todo.note = note
                todo.dateCreated = dateCreated
                todo.dateUpdated = dateUpdated
                todo.dueDate = dueDate
                todo.dueTime = time
                todo.remindMe = remindMe

                todoViewModel.updateTodo(todo)
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun showDeleteDialog(todo: Todo) {
        val dialogTitle = "Delete"
        val dialogMessage = "Are you sure want to delete this data?"
        val toastMessage = "Data has been deleted successfully"
        CustomConfirmDialog(this, dialogTitle, dialogMessage) {
            todoViewModel.deleteTodo(todo)
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        }.show()
    }

    private fun showDetailsDialog(todo: Todo) {
        val title = "Title: ${todo.title}"
        val dueDate = "Due date : ${todo.dueDate}, ${todo.dueTime}"
        val note = "Note: ${todo.note}"
        val dateCreated = "Date created: ${todo.dateCreated}"
        val dateUpdated = "Date updated: ${todo.dateUpdated}"
        val remindMe = "Reminder Active: ${todo.remindMe}"

        val strMessage = "$title\n$dueDate\n$note\n\n$dateCreated\n$dateUpdated\n$remindMe"

        AlertDialog.Builder(this).setMessage(strMessage).setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun setEmptyTextVisibility(state: Boolean) {
        if (state) tv_empty.visibility = View.VISIBLE
        else tv_empty.visibility = View.GONE
    }

    private fun setProgressbarVisibility(state: Boolean) {
        if (state) progressbar.visibility = View.VISIBLE
        else progressbar.visibility = View.INVISIBLE
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
