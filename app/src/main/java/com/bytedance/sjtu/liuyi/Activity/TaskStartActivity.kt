package com.bytedance.sjtu.liuyi.Activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.*
import androidx.annotation.RequiresApi
import com.bytedance.sjtu.liuyi.TodoListDBHelper
import com.bytedance.sjtu.liuyi.R

/*
* 该页面用于单击 task entry 时显示 task 详细内容
* 仅可展示，不可编辑
* */
class TaskStartActivity : AppCompatActivity() {
    private lateinit var task_title_textview: TextView
    private lateinit var task_detail_textview: TextView
    private lateinit var task_duration_textview: TextView
    private lateinit var task_status_textview : TextView
    private lateinit var task_show_toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var clock_btn : ToggleButton
    private lateinit var clock_reset_btn : Button
    private lateinit var task_clock : Chronometer
    private lateinit var task_tag : String
    private var base_time : Long = 0                                // 本次计时基准时间
    private var total_cur_duration : Long = 0                       // 用户本次在该界面下，总共 focus 的时间 (单位 ms)
    private var latest_duration : Long? = 0
    private val dbHelper = TodoListDBHelper(this, TODOLIST_DB_NAME)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_start)
        init()
        bindClickListener()
        val task_tag = intent.extras?.getString("task_tag").toString()
        if (!showContent(task_tag)) finish()
    }

    override fun onPause() {
        super.onPause()
        if (base_time > 0) {
            val current_time = SystemClock.elapsedRealtime()
            val tmpDuration = current_time - base_time
            total_cur_duration += tmpDuration
        }
        task_clock.setBase(SystemClock.elapsedRealtime())
        base_time = 0
        updateFocusTime()
        Toast.makeText(this, "本次专注时间已累加至总时间", Toast.LENGTH_SHORT).show()
    }

    // 绑定按钮点击事件
    private fun bindClickListener() {
        clock_btn.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                base_time = SystemClock.elapsedRealtime()
                task_clock.setBase(-1 * total_cur_duration + SystemClock.elapsedRealtime())         // 接着上次的计时开始
                task_clock.start()
                Toast.makeText(this, "计时开始", Toast.LENGTH_SHORT).show()
            } else {
                task_clock.stop()
                val current_time = SystemClock.elapsedRealtime()
                val tmpDuration = current_time - base_time
                total_cur_duration += tmpDuration
                base_time = 0
                Toast.makeText(this, "计时停止", Toast.LENGTH_SHORT).show()
            }
        }

        clock_reset_btn.setOnClickListener {
            clock_btn.isChecked = false
            task_clock.stop()
            if (base_time > 0) {
                val current_time = SystemClock.elapsedRealtime()
                val tmpDuration = current_time - base_time
                total_cur_duration += tmpDuration
            }
            task_clock.setBase(SystemClock.elapsedRealtime())
            base_time = 0
            updateFocusTime()
            Toast.makeText(this, "本次专注时间已累加至总时间", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateFocusTime() {
        latest_duration = latest_duration?.plus(total_cur_duration / 1000)
        // 更新数据库内容
        val db = dbHelper.writableDatabase
        val newValue = ContentValues().apply { put("task_duration", latest_duration) }
        db.update("task", newValue, "task_tag = ?", arrayOf(task_tag))
        total_cur_duration = 0
        // 更新显示
        val curText = convertSecondsToFormattedTime(latest_duration)
        runOnUiThread(Runnable {
            task_duration_textview.setText("当前任务已专注${curText}")
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun init() {
        task_title_textview = findViewById(R.id.task_title_show)
        task_detail_textview = findViewById(R.id.task_detail_show)
        task_duration_textview = findViewById(R.id.task_duration_show)
        task_clock = findViewById(R.id.task_clock)
        clock_btn = findViewById(R.id.clock_btn)
        clock_reset_btn = findViewById(R.id.clock_reset_button)

        // 初始化计时器
        task_clock.setCountDown(false)                                          // 设置为正计时

        // 设置左上角返回箭头
        task_show_toolbar = findViewById(R.id.task_show_toolbar)
        task_show_toolbar.setTitle("专注模式")
        task_show_toolbar.setNavigationIcon(R.drawable.to_left)
        setSupportActionBar(task_show_toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        task_show_toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "已返回", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showContent(Tag : String) : Boolean {
        val myMap = dbHelper.queryTaskInfo(Tag)
        if (myMap["task_exist"] == "True") {
            task_tag = myMap["task_tag"]!!
            task_title_textview.setText(myMap["task_title"])
            task_detail_textview.setText(myMap["task_detail"])
            latest_duration = myMap["task_duration"]?.toLong()                      // 获取总秒数
            val duration : String = convertSecondsToFormattedTime(latest_duration)
            task_duration_textview.setText("该任务已专注${duration}")                 // 表示为 xx 时 xx 分 xx 秒
            return true
        }
        else {
            Toast.makeText(this, "该任务不存在", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}