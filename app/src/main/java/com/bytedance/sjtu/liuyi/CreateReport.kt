package com.bytedance.sjtu.liuyi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.bytedance.sjtu.liuyi.Activity.TODOLIST_DB_NAME
import com.bytedance.sjtu.liuyi.Activity.TodoTaskEditActivity
import com.bytedance.sjtu.liuyi.DataClass.IdeaItem
import com.bytedance.sjtu.liuyi.DataClass.TaskElement
import org.w3c.dom.Text

class CreateReport : AppCompatActivity() {
    var curm: Int?=null
    var cury: Int?=null
    var curd: Int ?=null
    var mTitle:  TextView? = null
    //    TodoContent
    var UseDaySum: Int=0
    var TodoSum: Int=0
    var TodoCompleteSum: Int=0
    var DayTodoMax: Int=0
    var DayTodoMaxDay: Int=0
    var DayCompleteMax: Int=0
    var DayCompleteMaxDay: Int=0
    var DayFailMax:Int=0
    var DayFailMaxDay: Int=0
    //    MemoryContent
    var IdeaSum:Int=0
    var DayIdeaMax: Int=0
    var DayIdeaMaxDay: Int=0
    //    UI
    var mTodoSum: TextView?=null
    var mTodoMax: TextView?=null
    var mTodocMax: TextView?=null
    var mTodofMax: TextView?=null
    var mIdeaSum: TextView?=null
    var mIdeaMax: TextView?=null
    var museSum: TextView?=null

    var mretBut: Button?=null
    private val todolist_dbHelper : TodoListDBHelper = TodoListDBHelper(this, TODOLIST_DB_NAME)
    private val idealist_dbHelper : IdeaItemDBHelper = IdeaItemDBHelper(this,"idea.db",1)

    private var task_list=mutableListOf<TaskElement>()
    private var idea_list=mutableListOf<IdeaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_report)
        initData()
        initView()
    }
    fun initData(){
        cury=intent.extras?.getInt("year")
        curm=intent.extras?.getInt("month")
        curd=intent.extras?.getInt("day")

        DayTodoMaxDay=curd!!
        DayFailMaxDay=curd!!
        DayCompleteMaxDay=curd!!
        DayIdeaMaxDay=curd!!

        var time_date:String?=null
        var flag:Boolean=true
        var idea_len:Int?=null
        var task_tab:String?=null
        var tc:Int?=null
        var tf:Int?=null
        for(i in 1..31){
            flag=false
            tc=0
            tf=0
            Log.d("CreateReportActivity", i.toString()+ " looping")
            time_date=String.format("%04d-%02d-%02d",cury,curm,i)
            idea_list=idealist_dbHelper.getIdeaItemListByDate(time_date)
            task_list=todolist_dbHelper.queryTasksInOneDay(time_date)
            idea_len=idea_list.size
            if (idea_len>0) flag=true
            if (idea_len>DayIdeaMax) {
                DayIdeaMax=idea_len
                DayIdeaMaxDay=i
            }
            IdeaSum+=idea_len

            for(task_item in task_list){
                task_tab= task_item.task_tag.toString()
                if (todolist_dbHelper.queryTaskInfo(task_tab)["task_exist"]=="False")
                    continue
                else{
                    if (task_item.task_status=="done") tc++
                    else tf++
                }
            }
            if (tc+tf>0) flag=true
            if (tc>DayCompleteMax){
                DayCompleteMax=tc
                DayCompleteMaxDay=i
            }
            if (tf>DayFailMax){
                DayFailMax=tf
                DayFailMaxDay=i
            }
            if (tf+tc>DayTodoMax){
                DayTodoMax=tf+tc
                DayTodoMaxDay=i
            }
            TodoCompleteSum+=tc
            TodoSum+=tc+tf
            if (flag){
                UseDaySum++
            }
        }


    }

    fun initView(){
        mTitle=findViewById(R.id.report_title)
        museSum=findViewById(R.id.report_usesum)
        mIdeaMax=findViewById(R.id.report_ideamax)
        mIdeaSum=findViewById(R.id.report_ideasum)
        mTodoMax=findViewById(R.id.report_todomax)
        mTodoSum=findViewById(R.id.report_todosum)
        mTodocMax=findViewById(R.id.report_todocmax)
        mTodofMax=findViewById(R.id.report_todofmax)
        mretBut =findViewById(R.id.return_main)

        mretBut?.setOnClickListener{
//            val intent = Intent(this, MainActivity::class.java) // flag = 0 表示新建 task
//            startActivity(intent)
            finish()
        }

        mTitle!!.text = String.format("请查收来自留忆的%s月报告", curm)
        museSum!!.text = String.format("你本月总计使用留忆%s天",UseDaySum)
        mTodoSum!!.text = String.format("你这个月总共建立了%s个待办事项\n完成了其中的%s个",
            TodoSum,TodoCompleteSum)
        mTodoMax!!.text=String.format("你在%s月%s日建立了最多的待办事项\n竟高达%s个",curm,
            DayTodoMaxDay,DayTodoMax)
        mTodocMax!!.text=String.format("你在%s月%s日完成了最多的待办事项\n足足有%s个，你好棒！！",curm,
            DayCompleteMaxDay, DayCompleteMax)
        mTodofMax!!.text=String.format("你在%s月%s日有最多的待办事项未完成\n有%s个，下次加油呀！！",curm,
            DayFailMaxDay,DayFailMax)
        mIdeaSum!!.text=String.format("你这个月总共建立了%s条回忆感悟",
            IdeaSum)
        mIdeaMax!!.text=String.format("你在%s月%s日写了最多的感悟\n足足有%s条，那天一定很难忘吧",curm,DayIdeaMaxDay,
            DayIdeaMax)
    }
}