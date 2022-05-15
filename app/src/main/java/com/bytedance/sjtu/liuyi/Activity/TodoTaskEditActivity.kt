package com.bytedance.sjtu.liuyi.Activity

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.bytedance.sjtu.liuyi.TodoListDBHelper
import com.bytedance.sjtu.liuyi.R
/*
 * 长按进入未完成任务的编辑页面 （已完成任务不可编辑）
 */
class TodoTaskEditActivity : AppCompatActivity() {
    private lateinit var task_title_edittext: EditText
    private lateinit var task_detail_edittext: EditText
    private lateinit var task_edit_toolbar : androidx.appcompat.widget.Toolbar
    private var task_exist = false
    private lateinit var old_task_tag : String
    private lateinit var db : SQLiteDatabase
    private val dbHelper = TodoListDBHelper(this, TODOLIST_DB_NAME)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_task_edit)
        db = dbHelper.writableDatabase
        // flag = 0 表示新任务; flag = 1 表示编辑旧任务
        var toolbar_title : String
        val flag = intent.extras?.getString("flag").toString()
        if (flag == "0") {
            toolbar_title = "添加待办"
            old_task_tag = ""
        } else {
            toolbar_title = "编辑待办"
            old_task_tag = intent.extras?.getString("task_tag").toString()
        }
        // 如果是已经之前已经创建过的任务，当用户进入该任务编辑页面时，应该显示用户之前输入的内容
        task_exist = showContent(old_task_tag)
        task_edit_toolbar = findViewById(R.id.todo_task_edit_bar)
        // 设置顶部 toolBar 内容
        task_edit_toolbar.setTitle(toolbar_title)
        // 设置返回按钮
        task_edit_toolbar.setNavigationIcon(R.drawable.to_left)
        setSupportActionBar(task_edit_toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        task_edit_toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "已返回", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 添加保存按钮
        task_edit_toolbar.inflateMenu(R.menu.task_edit_menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 对于导航顶部栏的按钮，需要重写该方法来设置选中事件
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.getItemId()) {
            R.id.save_task -> {
                task_title_edittext = findViewById<EditText>(R.id.task_title_edit)
                task_detail_edittext = findViewById<EditText>(R.id.task_detail_edit)

                val dateFormatterForTag = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss")
                val dateFormatterForDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val new_task_tag = dateFormatterForTag.format(LocalDateTime.now())
                val task_date = dateFormatterForDate.format(LocalDateTime.now())
                var task_title = task_title_edittext.text.toString()
                var task_detail = task_detail_edittext.text.toString()

                // 设置默认值
                if (task_title == "") { task_title = "无主题" }
                if (task_detail == "") { task_detail = "无内容" }

                val newTask = ContentValues().apply {
                    put("task_title", task_title)
                    put("task_detail", task_detail)
                    put("task_status", "todo")
                }

                if (task_exist) {
                    db.update("task", newTask, "task_tag = ?", arrayOf(old_task_tag))
                } else {
                    newTask.put("task_date", task_date)
                    newTask.put("task_tag", new_task_tag)
                    newTask.put("task_duration", "0")
                    db.insert("task", null, newTask)
                }
                Log.d ("#######", newTask.toString())
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showContent(task_tag: String): Boolean {
        val myMap = dbHelper.queryTaskInfo(task_tag)
        Log.d("########### pos_10", myMap.toString())
        if (myMap["task_exist"] == "True") {
            task_title_edittext = findViewById<EditText>(R.id.task_title_edit)
            task_title_edittext.setText(myMap["task_title"])

            task_detail_edittext = findViewById<EditText>(R.id.task_detail_edit)
            task_detail_edittext.setText(myMap["task_detail"])
            return true
        } else return false
    }
}