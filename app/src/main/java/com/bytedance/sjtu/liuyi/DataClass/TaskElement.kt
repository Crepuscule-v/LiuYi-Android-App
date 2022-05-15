package com.bytedance.sjtu.liuyi.DataClass

data class TaskElement (
    var task_title : String?,           // 任务标题
    var task_detail : String?,          // 任务描述
    var task_tag : String?,             // 任务在数据库唯一标识
    var task_status : String?,          // 任务完成状态
    var task_duration: String?,         // 任务专注时间
    var task_date: String?              // 任务创建日期
)