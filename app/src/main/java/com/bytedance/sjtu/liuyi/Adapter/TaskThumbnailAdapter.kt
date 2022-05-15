package com.bytedance.sjtu.liuyi.Adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sjtu.liuyi.MainActivity
import com.bytedance.sjtu.liuyi.DataClass.TaskElement
import com.bytedance.sjtu.liuyi.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import android.content.Intent
import android.util.Log
import android.widget.ImageButton
import com.bytedance.sjtu.liuyi.Activity.*
import com.bytedance.sjtu.liuyi.TodoListDBHelper
import org.w3c.dom.Text

/**
 * List: 有序接口, 只能读取, 不能更改元素;
 * MutableList: 有序接口, 可以读写与更改, 删除, 增加元素.
 */

class TaskThumbnailAdapter(activity: MainActivity) : RecyclerView.Adapter<TaskThumbnailAdapter.TaskElementViewHolder>() {
    private var taskList = mutableListOf<TaskElement>()
    private val main_activity = activity

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskElementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_tumbnail_recyclerview_entry, parent, false)       // 内容横向铺满
        val viewHolder = TaskElementViewHolder(view)
        var taskStatusCheckbox = view.findViewById<CheckBox>(R.id.task_status_checkbox)
        var delete_single_task_btn = view.findViewById<ImageButton>(R.id.delete_single_task_btn)
        var dbHelper = TodoListDBHelper(main_activity, TODOLIST_DB_NAME)
        var db = dbHelper.openDB()

        // todo: 添加对 checkbox 的点击监听事件
        taskStatusCheckbox.setOnClickListener {
            main_activity.changeTaskStatus(viewHolder.task_entry_date.text.toString(), taskStatusCheckbox)
        }

        // task item 长按跳转到编辑页, 仅未完成事件可编辑，已完成事件仅可查看
        viewHolder.itemView.setOnLongClickListener {
            if (taskStatusCheckbox.isChecked) {
                val intent = Intent(main_activity, DoneTaskShowActivity::class.java)
                intent.putExtra("flag", "1")
                intent.putExtra("task_tag", viewHolder.task_entry_date.text.toString())
                main_activity.startActivity(intent)
                false
            }
            else {
                val intent = Intent(main_activity, TodoTaskEditActivity::class.java)
                intent.putExtra("flag", "1")
                intent.putExtra("task_tag", viewHolder.task_entry_date.text.toString())
                main_activity.startActivity(intent)
                false
            }
        }

        // task item 点击跳转到任务启动页，仅未完成事件可启动
        viewHolder.itemView.setOnClickListener {
            if (!taskStatusCheckbox.isChecked) {
                val intent = Intent(main_activity, TaskStartActivity::class.java)
                intent.putExtra("task_tag", viewHolder.task_entry_date.text.toString())
                intent.putExtra("flag", "1")
                main_activity.startActivity(intent)
            }
        }

        delete_single_task_btn.setOnClickListener {
            db.delete("task", "task_tag = ? ", arrayOf(viewHolder.task_entry_date.text.toString()))
            main_activity.updateTaskListFromDB()
            updateTaskList(main_activity.taskList)
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskElementViewHolder, idx: Int) {
        holder.task_entry_date.setText(taskList[idx].task_tag)
        holder.task_entry_title.setText(taskList[idx].task_title)
        var focus_time = convertSecondsToFormattedTime(taskList[idx].task_duration?.toLong())
        holder.task_entry_duration.setText("已专注${focus_time}")
        holder.task_entry_status.isChecked = taskList[idx].task_status != "todo"
    }

    override fun getItemCount(): Int = taskList.size

    @SuppressLint("SimpleDateFormat")
    val myComparator : Comparator<TaskElement> = Comparator { task_1, task_2 ->
        var result : Int
        if (task_1.task_status == task_2.task_status) {
            val dateformatter : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
            val date_1 : Date = dateformatter.parse(task_1.task_tag)
            val date_2 : Date = dateformatter.parse(task_2.task_tag)
            if (date_1.before(date_2)) result = -1
            else result = 1
        } else {
            if (task_1.task_status == "todo") result =  -1
            else result = 1
        }
        return@Comparator result
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTaskList(myList : List<TaskElement>) {
        taskList.clear()
        taskList.addAll(myList)
        taskList.sortWith(myComparator)
        Log.d("############", taskList.toString())
        notifyDataSetChanged()                          // 当数据发生变化时，更新 view
    }

    class TaskElementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val task_entry_date : TextView = view.findViewById<TextView>(R.id.task_entry_date)
        val task_entry_title: TextView = view.findViewById<TextView>(R.id.task_entry_title)
        val task_entry_status: CheckBox = view.findViewById<CheckBox>(R.id.task_status_checkbox)
        val task_entry_duration : TextView = view.findViewById<TextView>(R.id.task_entry_duration)
    }
}