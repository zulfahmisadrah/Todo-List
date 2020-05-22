package com.zulfahmi.todolist.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zulfahmi.todolist.R
import com.zulfahmi.todolist.model.Todo
import kotlinx.android.synthetic.main.item_row_todo.view.*

class TodoAdapter(private val context: Context?, private val listener: (Todo, Int) -> Unit): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){
    private var todoList = listOf<Todo>()

    fun setTodoList(todoList: List<Todo>) {
        this.todoList = todoList
        notifyDataSetChanged()
        Log.d("cek", todoList.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder =
        TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_row_todo, parent, false))

    override fun getItemCount(): Int = todoList.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        if (context != null) {
            holder.bindItem(todoList[position], listener)
        }
    }

    class TodoViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItem(todo: Todo, listener: (Todo, Int) -> Unit) {
            val date = if (todo.dateUpdated != todo.dateCreated) "Updated at ${todo.dateUpdated}" else "Created at ${todo.dateCreated}"
            val dueDate = "Due ${todo.dueDate}, ${todo.dueTime}"

            itemView.tv_title.text = todo.title
            itemView.tv_note.text = todo.note
            itemView.tv_due_date.text = dueDate
            itemView.tv_date_created_updated.text = date

            itemView.setOnClickListener{
                listener(todo, layoutPosition)
            }

        }
    }
}