package com.bytedance.sjtu.liuyi

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bytedance.sjtu.liuyi.DataClass.TaskElement

/*
 * 数据库中新建 table : task
 */

class TodoListDBHelper(val context: Context, name: String, version: Int = 1): SQLiteOpenHelper(context, name, null, version) {
    private val TodoListCreate by lazy { "create table task(" + " id integer primary key autoincrement, " + " task_tag text, " + " task_title text, " +  " task_status text, " + " task_detail text, " + " task_duration text, " + " task_date text) " }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TodoListCreate)
        Toast.makeText(context, "Todo List 初始化完成", Toast.LENGTH_SHORT).show()
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    public fun openDB() : SQLiteDatabase {
        return this.writableDatabase
    }

    // 根据 task_tag 查询 task 其它内容, 返回 Map (String -> String)
    fun queryTaskInfo(task_tag : String) : MutableMap<String?, String?>{
        Log.d("########## Pos_4", task_tag)
        var myMap = mutableMapOf<String?, String?>(Pair("task_exist", "False"))
        val cursor = this.writableDatabase.query("task", null, "task_tag = ?", arrayOf(task_tag), null, null, null)
        Log.d("######### Pos_6", cursor.toString())
        while (cursor.moveToNext()) {
            val task_title: String? = cursor.getString(cursor.getColumnIndexOrThrow("task_title"))
            val task_detail : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_detail"))
            val task_status : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_status"))
            val task_duration : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_duration"))
            val task_date : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_date"))
            myMap.put("task_title", task_title)
            myMap.put("task_detail", task_detail)
            myMap.put("task_duration", task_duration)
            myMap.put("task_status", task_status)
            myMap.put("task_date", task_date)
            myMap.put("task_tag", task_tag)
            myMap["task_exist"] = "True"
        }
        Log.d("######## Pos_5", myMap.toString())
        return myMap
    }

    // 返回某一天全部任务
    fun queryTasksInOneDay (date : String) : MutableList<TaskElement> {
        var taskListInOneDay : MutableList<TaskElement> = mutableListOf()
        val cursor = this.readableDatabase.query("task", null, "task_date = ?", arrayOf(date), null, null, null)
        while (cursor.moveToNext()) {
            val task_title: String? = cursor.getString(cursor.getColumnIndexOrThrow("task_title"))
            val task_detail : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_detail"))
            val task_status : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_status"))
            val task_duration : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_duration"))
            val task_date : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_date"))
            val task_tag : String? = cursor.getString(cursor.getColumnIndexOrThrow("task_tag"))
            val tmpTask = TaskElement(task_title, task_detail, task_tag, task_status, task_duration, task_date)
            taskListInOneDay.add(tmpTask)
        }
        return taskListInOneDay
    }
}