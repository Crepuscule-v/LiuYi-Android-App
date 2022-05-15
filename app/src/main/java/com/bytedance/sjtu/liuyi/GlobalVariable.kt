package com.bytedance.sjtu.liuyi.Activity

/*
 * 该文件用来保存全局变量和函数
 */

const val TODOLIST_DB_NAME = "ToDoList_v1.db"
const val IDEA_DB_NAME = "IdeaList_v1.db"
const val TAG_PATTERN = "yyyy-MM-dd hh:mm:ss"

fun convertSecondsToFormattedTime (seconds : Long?) : String {
    val hour : String = (seconds!! / 3600).toString()
    val minute : String =  (seconds % 3600 / 60).toString()
    val sec : String = (seconds % 3600 % 60).toString()
    return " ${hour} 时 ${minute} 分 ${sec} 秒 "
}