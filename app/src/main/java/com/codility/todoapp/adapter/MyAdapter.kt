package com.codility.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.codility.todoapp.R
import com.codility.todoapp.model.Todo
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * Created by Govind on 2/16/2018.
 */
class MyAdapter(private val todoList: ArrayList<Todo>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private var listener: OnClickListener? = null

    fun setListener(clickListener: OnClickListener) {
        this.listener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo: Todo = todoList[position]
        holder.bindItems(todo)

        holder.itemView.setOnClickListener(View.OnClickListener {
            if (listener != null) {
                listener!!.onItemClick(todo, position)
            }
        })

        holder.itemView.btDelete.setOnClickListener(View.OnClickListener {
            if (listener != null) {
                listener!!.onItemDelete(todo)
            }
        })
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.list_item, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(todo: Todo) {
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc)
            val tvTimestamp = itemView.findViewById<TextView>(R.id.tvTimestamp)
            tvTitle.text = todo.title
            tvDesc.text = todo.desc
            tvTimestamp.text = todo.timestamp
        }
    }

    interface OnClickListener {
        fun onItemClick(todo: Todo, position: Int)
        fun onItemDelete(todo: Todo)
    }
}