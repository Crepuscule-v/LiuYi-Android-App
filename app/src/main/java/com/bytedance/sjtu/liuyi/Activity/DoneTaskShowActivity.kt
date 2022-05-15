package com.bytedance.sjtu.liuyi.Activity

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bytedance.sjtu.liuyi.TodoListDBHelper
import com.bytedance.sjtu.liuyi.R

/*
 * 长按进入已完成任务的查看页面
 */
class DoneTaskShowActivity : AppCompatActivity() {
    private lateinit var done_task_show_title: TextView
    private lateinit var done_task_show_detail: TextView
    private lateinit var done_task_show_duration: TextView
    private lateinit var done_task_show_toolbar : androidx.appcompat.widget.Toolbar
    private val dbHelper = TodoListDBHelper(this, TODOLIST_DB_NAME)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done_task_show)
        val task_tag = intent.extras?.getString("task_tag").toString()

        if (!showContent(task_tag)) {
            finish()
            Toast.makeText(this, "任务不存在", Toast.LENGTH_SHORT).show()
        }

        done_task_show_toolbar = findViewById(R.id.done_task_show_toolbar)
        done_task_show_toolbar.setTitle("任务完成情况")
        done_task_show_toolbar.setNavigationIcon(R.drawable.to_left)
        setSupportActionBar(done_task_show_toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        // 设置左上角返回箭头
        done_task_show_toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "已返回", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showContent(task_tag: String): Boolean {
        val myMap = dbHelper.queryTaskInfo(task_tag)
        if (myMap["task_exist"] == "True") {
            done_task_show_title = findViewById<TextView>(R.id.done_task_show_title)
            done_task_show_title.setText(myMap["task_title"])

            done_task_show_detail = findViewById<TextView>(R.id.done_task_show_detail)
            done_task_show_detail.setText(myMap["task_detail"])

            done_task_show_duration = findViewById<TextView>(R.id.done_task_show_duration)
            val duration = myMap["task_duration"]?.toLong()
            done_task_show_duration.setText("该任务总计专注时间 :" + convertSecondsToFormattedTime((duration)))
            return true
        } else return false
    }

    private fun convertSecondsToFormattedTime (seconds : Long?) : String {
        val hour : String = (seconds!! / 3600).toString()
        val minute : String =  (seconds % 3600 / 60).toString()
        val sec : String = (seconds % 3600 % 60).toString()
        return " ${hour} 时 ${minute} 分 ${sec} 秒 "
    }
}