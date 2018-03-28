package com.codility.todoapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codility.recyclerview.MyAdapter
import com.codility.todoapp.helper.DBHelper
import com.codility.todoapp.model.Todo
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import java.util.*

class ScrollingActivity : AppCompatActivity(), MyAdapter.OnClickListener {

    override fun onItemDelete(todo: Todo) {
        deleteConfirmation(todo)
    }

    override fun onItemClick(todo: Todo, position: Int) {
        showNoteDialog(true, todo, position)
    }

    private var myAdapter: MyAdapter? = null
    private var dbHelper: DBHelper? = null
    private var todoList = ArrayList<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        dbHelper = DBHelper(this)

        fab.setOnClickListener {
            showNoteDialog(false, null, -1)
        }
        //Set the TodoList in myAdapter
        getTodoList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (myAdapter != null) {
            getTodoList()
            myAdapter!!.notifyDataSetChanged()
        }
    }

    private fun getTodoList() {
        todoList = dbHelper!!.allNotes
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        myAdapter = MyAdapter(todoList)
        myAdapter!!.setListener(this)
        recyclerView.adapter = myAdapter
    }

    private fun deleteConfirmation(todo: Todo) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Confirm Delete...")
        alertDialog.setMessage("Are you sure you want to delete this?")
        alertDialog.setIcon(R.drawable.ic_delete)
        alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
            dbHelper!!.deleteTodo(todo)
            getTodoList()  // refreshing the list
        })

        alertDialog.setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel() //Cancel the dialog
        })
        alertDialog.show()
    }

    /**
     * Shows alert dialog with EditText options to enter / edit  a note.
     * when shouldUpdate=true, it automatically displays old note and changes the  button text to UPDATE
     */
    private fun showNoteDialog(shouldUpdate: Boolean, todo: Todo?, position: Int) {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.add_todo, null)

        val alertDialogView = AlertDialog.Builder(this).create()
        alertDialogView.setView(view)

        val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
        val edTitle = view.findViewById<EditText>(R.id.edTitle)
        val edDesc = view.findViewById<EditText>(R.id.edDesc)
        val btAddUpdate = view.findViewById<Button>(R.id.btAddUpdate)
        val btCancel = view.findViewById<Button>(R.id.btCancel)
        if (shouldUpdate) btAddUpdate.text = "Update" else btAddUpdate.text = "Save"

        if (shouldUpdate && todo != null) {
            edTitle.setText(todo.title)
            edDesc.setText(todo.desc)
        }

        btAddUpdate.setOnClickListener(View.OnClickListener {
            val tName = edTitle.text.toString()
            val descName = edDesc.text.toString()

            if (TextUtils.isEmpty(tName)) {
                Toast.makeText(this, "Enter Your Title!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(descName)) {
                Toast.makeText(this, "Enter Your Description!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            // check if user updating Todos
            if (shouldUpdate && todo != null) {
                updateNote(Todo(tName, descName), position)      // update note by it's id
            } else {
                createNote(Todo(tName, descName))   // create new note
            }
            alertDialogView.dismiss()
        })

        btCancel.setOnClickListener(View.OnClickListener {
            alertDialogView.dismiss()
        })
        tvHeader.text = if (!shouldUpdate) getString(R.string.lbl_new_todo_title) else getString(R.string.lbl_edit_todo_title)

        alertDialogView.setCancelable(false)
        alertDialogView.show()
    }

    /**
     * Inserting new note in db and refreshing the list
     */
    private fun createNote(todo: Todo) {
        val id = dbHelper!!.insertTodo(todo)    // inserting note in db and getting newly inserted note id
        val new = dbHelper!!.getTodo(id)  // get the newly inserted note from db
        if (new != null) {
            todoList.add(0, new)    // adding new note to array list at 0 position
            myAdapter!!.notifyDataSetChanged()  // refreshing the list
        }
    }

    /**
     * Updating note in db and updating item in the list by its position
     */
    private fun updateNote(t: Todo, position: Int) {
        val todo = todoList[position]
        todo.title = t.title    // updating title
        todo.desc = t.desc  // updating description
        dbHelper!!.updateTodo(todo) // updating note in db
        todoList[position] = todo  // refreshing the list
        myAdapter!!.notifyItemChanged(position)
    }
}