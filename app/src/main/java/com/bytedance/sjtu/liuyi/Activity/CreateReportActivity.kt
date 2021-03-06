package com.bytedance.sjtu.liuyi.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.bytedance.sjtu.liuyi.DBHelper.IdeaItemDBHelper
import com.bytedance.sjtu.liuyi.DBHelper.TodoListDBHelper
import com.bytedance.sjtu.liuyi.DataClass.IdeaItem
import com.bytedance.sjtu.liuyi.DataClass.TaskElement
import com.bytedance.sjtu.liuyi.R

class CreateReportActivity : AppCompatActivity() {
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
    private val idealist_dbHelper : IdeaItemDBHelper = IdeaItemDBHelper(this, IDEA_DB_NAME,1)

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
//            val intent = Intent(this, MainActivity::class.java) // flag = 0 ???????????? task
//            startActivity(intent)
            finish()
        }

        mTitle!!.text = String.format("????????????????????????%s?????????", curm)
        museSum!!.text = String.format("???????????????????????????%s???",UseDaySum)
        mTodoSum!!.text = String.format("???????????????????????????%s???????????????\n??????????????????%s???",
            TodoSum,TodoCompleteSum)
        mTodoMax!!.text=String.format("??????%s???%s?????????????????????????????????\n?????????%s???",curm,
            DayTodoMaxDay,DayTodoMax)
        mTodocMax!!.text=String.format("??????%s???%s?????????????????????????????????\n?????????%s?????????????????????",curm,
            DayCompleteMaxDay, DayCompleteMax)
        mTodofMax!!.text=String.format("??????%s???%s????????????????????????????????????\n???%s???????????????????????????",curm,
            DayFailMaxDay,DayFailMax)
        mIdeaSum!!.text=String.format("???????????????????????????%s???????????????",
            IdeaSum)
        mIdeaMax!!.text=String.format("??????%s???%s????????????????????????\n?????????%s??????????????????????????????",curm,DayIdeaMaxDay,
            DayIdeaMax)
    }
}