package com.bytedance.sjtu.liuyi.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sjtu.liuyi.Activity.AllTaskActivity
import com.bytedance.sjtu.liuyi.DataClass.TaskElement
import com.bytedance.sjtu.liuyi.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

/*
** 该 adapter 用于展示当天全部任务
 */
class AllTaskAdapter (activity : AllTaskActivity) : RecyclerView.Adapter<AllTaskAdapter.SingleTaskViewHolder>() {
    private var taskList = mutableListOf<TaskElement>()     // 根据 task_date 和 task_status 排序

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_single_task_entry, parent, false)       // 内容横向铺满
        val viewHolder = SingleTaskViewHolder(view)
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SingleTaskViewHolder, position: Int) {
        holder.task_date.text = taskList[position].task_tag.toString()
        holder.task_detail.text = taskList[position].task_detail.toString()
        holder.task_title.text = taskList[position].task_title.toString()
        val duration = taskList[position].task_duration.toString()
        holder.task_duration.text = "已专注" + convertSecondsToFormattedTime(duration.toLong())
        // todo：根据完成状态设置背景
//        if (taskList[position].task_status == "todo") holder.task_card_bg.setBackgroundResource(R.drawable.card_bg_bright)
//        else holder.task_card_bg.setBackgroundResource(R.drawable.card_bg_dark)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun updateTaskList (myList : List<TaskElement>) {
        taskList.clear()
        taskList.addAll(myList)
        taskList.sortWith(myComparator)
    }

    inner class SingleTaskViewHolder (view : View) : RecyclerView.ViewHolder(view) {
        val task_date : TextView = view.findViewById<TextView>(R.id.single_task_date)
        val task_detail : TextView = view.findViewById<TextView>(R.id.single_task_detail)
        val task_title : TextView = view.findViewById<TextView>(R.id.single_task_title)
        val task_duration : TextView = view.findViewById<TextView>(R.id.single_task_duration)
        val task_card_bg : ImageView = view.findViewById<ImageView>(R.id.task_card_bg)
    }

    private fun convertSecondsToFormattedTime (seconds : Long?) : String {
        val hour : String = (seconds!! / 3600).toString()
        val minute : String =  (seconds % 3600 / 60).toString()
        val sec : String = (seconds % 3600 % 60).toString()
        return " ${hour} 时 ${minute} 分 ${sec} 秒 "
    }
}