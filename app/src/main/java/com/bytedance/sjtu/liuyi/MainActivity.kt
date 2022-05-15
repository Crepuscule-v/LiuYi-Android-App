package com.bytedance.sjtu.liuyi
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.haibin.calendarview.CalendarView.*
import com.haibin.calendarview.TrunkBranchAnnals
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sjtu.liuyi.Activity.*
import com.bytedance.sjtu.liuyi.Adapter.TaskThumbnailAdapter
import com.bytedance.sjtu.liuyi.DataClass.TaskElement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity(), OnCalendarSelectListener, OnCalendarLongClickListener, OnMonthChangeListener, OnYearChangeListener, OnWeekChangeListener, OnViewChangeListener, OnCalendarInterceptListener, OnYearViewChangeListener, DialogInterface.OnClickListener, View.OnClickListener {
    var mTextMonthDay: TextView? = null
    var mTextYear: TextView? = null
    var mTextLunar: TextView? = null
    var mCalendarView: CalendarView? = null
    var mRelativeTool: RelativeLayout? = null

    private var caly:Int?=null
    private var calm:Int?=null
    private var cald:Int?=null
    private var calenderYear : String? = null
    private var calenderMonth : String? = null
    private var calenderDay : String? = null
    private lateinit var allTaskAtOneDayBtn: ImageButton
    private lateinit var addNewTaskBtn : ImageButton
    private lateinit var taskThumbnailAdapter: TaskThumbnailAdapter
    var taskList = mutableListOf<TaskElement>()
    var isPause = false
    var ideabutton: Button?=null

    var createIdeaItemButton: ImageButton?=null
    var viewIdeaItemListViewButton : ImageButton? = null
    var createbutton: ImageButton?=null
    var clear_all_task_of_day_btn : ImageButton?=null
    private var mYear = 0
//        var mCalendarLayout: CalendarLayout? = null

    private lateinit var todolist_db : SQLiteDatabase
    private val todolist_dbHelper : TodoListDBHelper = TodoListDBHelper(this, TODOLIST_DB_NAME)

    private val ideaItemRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            IdeaItemCreationActivity.IdeaItemCreationSuccessCode -> {
                Log.d("MainActivity", "Create Idea Item Success")
            }
            IdeaItemCreationActivity.IdeaItemCreationFailCode -> {
                Log.d("MainActivity", "Create Idea Item Fail")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 延长启动页时间
        val contentView: View = findViewById(android.R.id.content)
        contentView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                Timer().schedule(object:TimerTask(){
                    override fun run() {
                    }
                }, Date(), 2000)
                return true
            }
        })
        todolist_db = todolist_dbHelper.openDB()
        initView()
        setRecyclerView()
        setClickEvents()
        initData()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (isPause) {
            isPause = false
            updateTaskListFromDB()
            taskThumbnailAdapter.updateTaskList(taskList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setRecyclerView() {
        val taskThumbnailRecyclerView = findViewById<RecyclerView>(R.id.task_thumbnail_recyclerview)
        val taskThumbnailLinearLayoutManager = LinearLayoutManager(this)
        taskThumbnailLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        taskThumbnailRecyclerView.layoutManager = taskThumbnailLinearLayoutManager
        taskThumbnailAdapter = TaskThumbnailAdapter(this)
        updateTaskListFromDB()
        taskThumbnailAdapter.updateTaskList(taskList)
        taskThumbnailRecyclerView.adapter = taskThumbnailAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTaskListFromDB() {
        val myDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = myDateFormatter.format(LocalDateTime.now())
        taskList.clear()
        taskList.addAll(todolist_dbHelper.queryTasksInOneDay(today))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickEvents () {
        // 添加新任务按钮
        addNewTaskBtn.setOnClickListener {
            val intent = Intent(this, TodoTaskEditActivity::class.java)
            intent.putExtra("flag", "0")            // flag = 0 表示新建 task
            startActivity(intent)
        }

        // 查看详情按钮
        allTaskAtOneDayBtn.setOnClickListener{
//            if (calenderYear == null) {
//                calenderYear = mCalendarView!!.curYear.toString()
//            }
//            if (calenderMonth == null) {
//                calenderMonth = mCalendarView!!.curMonth.toString()
//            }
//            if (calenderDay == null) {
//                calenderDay = mCalendarView!!.curDay.toString()
//            }
            val dateFormatterForDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val new_task_tag = dateFormatterForDate.format(LocalDateTime.now())
            val intent = Intent(this, AllTaskActivity::class.java)
            intent.putExtra("task_date",new_task_tag)
//            intent.putExtra("year",calenderYear)
//            intent.putExtra("month",calenderMonth)
//            intent.putExtra("day",calenderDay)
            startActivity(intent)
        }

        createbutton!!.setOnClickListener{
            startActivity(Intent().apply {
                setClass(this@MainActivity, CreateReport::class.java)
                putExtra("year",caly)
                putExtra("month",calm)
                putExtra("day",cald)
            })
        }


        viewIdeaItemListViewButton!!.setOnClickListener {
//            if (calenderYear == null) {
//                calenderYear = mCalendarView!!.curYear.toString()
//            }
//            if (calenderMonth == null) {
//                calenderMonth = mCalendarView!!.curMonth.toString()
//            }
//            if (calenderDay == null) {
//                calenderDay = mCalendarView!!.curDay.toString()
//            }
            val dateFormatterForDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val new_task_date = dateFormatterForDate.format(LocalDateTime.now())
            startActivity(Intent().apply {
                setClass(this@MainActivity, IdeaActivity::class.java)
//                putExtra("year",calenderYear)
//                putExtra("month",calenderMonth)
//                putExtra("day",calenderDay)
                putExtra("task_date", new_task_date)
            })
        }

        clear_all_task_of_day_btn?.setOnClickListener() {
            val dateFormatterForDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val task_date = dateFormatterForDate.format(LocalDateTime.now())
            todolist_db.delete("task", "task_date = ?", arrayOf(task_date))
            updateTaskListFromDB()
            taskThumbnailAdapter.updateTaskList(taskList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    fun initView() {
        mTextMonthDay = findViewById(R.id.tv_month_day)
        mTextYear = findViewById(R.id.tv_year)
        mRelativeTool = findViewById(R.id.rl_tool)
        mCalendarView = findViewById(R.id.calendarView)
        allTaskAtOneDayBtn = findViewById(R.id.all_task_at_one_day_btn)
        createbutton=findViewById(R.id.createreport)
        addNewTaskBtn = findViewById(R.id.add_new_task)
        clear_all_task_of_day_btn = findViewById(R.id.clear_all_task_of_day_btn)
//        createIdeaItemButton = findViewById(R.id.idea_btn)
        viewIdeaItemListViewButton = findViewById(R.id.idea_btn)
        mCalendarView!!.setOnYearChangeListener(this)
        mCalendarView!!.setOnCalendarSelectListener(this)
        mCalendarView!!.setOnMonthChangeListener(this)
        mCalendarView!!.setOnCalendarLongClickListener(this, true)
        mCalendarView!!.setOnWeekChangeListener(this)
        mCalendarView!!.setOnYearViewChangeListener(this)

        //设置日期拦截事件，仅适用单选模式，当前无效
        mCalendarView!!.setOnCalendarInterceptListener(this)
        mCalendarView!!.setOnViewChangeListener(this)
        mTextYear!!.text = mCalendarView!!.curYear.toString()
        mYear = mCalendarView!!.curYear
        mTextMonthDay!!.text = mCalendarView!!.curMonth.toString() + "月" + mCalendarView!!.curDay + "日"

    }

    fun initData() {
        val year = mCalendarView!!.curYear
        val month = mCalendarView!!.curMonth

    }

    override fun onClick(dialog: DialogInterface, which: Int) {}
    override fun onClick(v: View) {
//        switch (v.getId()) {
//            case R.id.ll_flyme:
//                MeiZuActivity.show(this);
//                //CalendarActivity.show(this);
//                break;
//        }
    }

    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int,
        color: Int,
        text: String
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = color //如果单独标记颜色、则会使用这个颜色
        calendar.scheme = text
        return calendar
    }

    override fun onCalendarOutOfRange(calendar: Calendar) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        //Log.e("onDateSelected", "  -- " + calendar.getYear() + "  --  " + calendar.getMonth() + "  -- " + calendar.getDay());
//        mTextLunar!!.visibility = VISIBLE
        calenderYear = calendar.year.toString()
        calenderMonth = calendar.month.toString()
        calenderDay = calendar.day.toString()

        caly=calendar.year
        calm=calendar.month
        cald=calendar.day
        mTextYear!!.visibility = VISIBLE
        mTextMonthDay!!.text = calendar.month.toString() + "月" + calendar.day + "日"
        mTextYear!!.text = calendar.year.toString()
//        mTextLunar!!.text = calendar.lunar
        mYear = calendar.year
        if (isClick) {
            Toast.makeText(this, getCalendarText(calendar), Toast.LENGTH_SHORT).show()
        }
        //        Log.e("lunar "," --  " + calendar.getLunarCalendar().toString() + "\n" +
//        "  --  " + calendar.getLunarCalendar().getYear());
        Log.e(
            "onDateSelected", "  -- " + calendar.year +
                    "  --  " + calendar.month +
                    "  -- " + calendar.day +
                    "  --  " + isClick + "  --   " + calendar.scheme
        )
        Log.e(
            "onDateSelected", "  " + mCalendarView!!.selectedCalendar.scheme +
                    "  --  " + mCalendarView!!.selectedCalendar.isCurrentDay
        )
        Log.e("干支年纪 ： ", " -- " + TrunkBranchAnnals.getTrunkBranchYear(calendar.lunarCalendar.year))
    }

    override fun onCalendarLongClickOutOfRange(calendar: Calendar) {
        Toast.makeText(
            this,
            String.format("%s : LongClickOutOfRange", calendar),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCalendarLongClick(calendar: Calendar) {
        if (calenderYear == null) {
            calenderYear = mCalendarView!!.curYear.toString()
        }
        if (calenderMonth == null) {
            calenderMonth = mCalendarView!!.curMonth.toString()
        }
        if (calenderDay == null) {
            calenderDay = mCalendarView!!.curDay.toString()
        }

        val task_date=String.format("%04d-%02d-%02d",calendar.year,calendar.month,calendar.day)
        Toast.makeText(
            this, task_date, Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this, AllTaskActivity::class.java)
        intent.putExtra("task_date", task_date)            // flag = 0 表示新建 task
        intent.putExtra("year", calenderYear)
        intent.putExtra("month",calenderMonth)
        intent.putExtra("day",calenderDay)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun onMonthChange(year: Int, month: Int) {
        Log.e("onMonthChange", "  -- $year  --  $month")
        val calendar = mCalendarView!!.selectedCalendar
//        mTextLunar!!.visibility = VISIBLE
        mTextYear!!.visibility = VISIBLE
        mTextMonthDay!!.text = calendar.month.toString() + "月" + calendar.day + "日"
        mTextYear!!.text = calendar.year.toString()
//        mTextLunar!!.text = calendar.lunar
        mYear = calendar.year
    }

    override fun onViewChange(isMonthView: Boolean) {
        Log.e("onViewChange", "  ---  " + if (isMonthView) "月视图" else "周视图")
    }

    override fun onWeekChange(weekCalendars: List<Calendar>) {
        for (calendar in weekCalendars) {
            Log.e("onWeekChange", calendar.toString())
        }
    }

    override fun onYearViewChange(isClose: Boolean) {
        Log.e("onYearViewChange", "年视图 -- " + if (isClose) "关闭" else "打开")
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     */
    override fun onCalendarIntercept(calendar: Calendar): Boolean {
        Log.e("onCalendarIntercept", calendar.toString())
        val day = calendar.day
        return day == 1 || day == 3 || day == 6 || day == 11 || day == 12 || day == 15 || day == 20 || day == 26
    }

    override fun onCalendarInterceptClick(calendar: Calendar, isClick: Boolean) {
        Toast.makeText(this, calendar.toString() + "拦截不可点击", Toast.LENGTH_SHORT).show()
    }

    override fun onYearChange(year: Int) {
        mTextMonthDay!!.text = year.toString()
        Log.e("onYearChange", " 年份变化 $year")
    }

    private fun getIdeaItemCountForDate(date : String) : Int {
        val dbHelper = IdeaItemDBHelper(this, "idea.db", 1)
        val ideaItemList = dbHelper.getIdeaItemListByDate(date)
        return ideaItemList.size
    }

    private fun calenderViewDate2IdeaItemDatabaseQueryDate() : String {
        if (calenderYear == null) {
            calenderYear = mCalendarView!!.curYear.toString()
        }
        if (calenderMonth == null) {
            calenderMonth = mCalendarView!!.curMonth.toString()
        }
        if (calenderDay == null) {
            calenderDay = mCalendarView!!.curDay.toString()
        }
        val year = calenderYear
        val month = calenderMonth!!.toInt()
        val day = calenderDay!!.toInt()
        var dateQueryStr = "$year-"
        dateQueryStr += if (month < 10) {
            "0$month-"
        } else {
            "$month-"
        }
        dateQueryStr += if (day < 10) {
            "0$day"
        } else {
            "$day"
        }
        return dateQueryStr
    }

    companion object {
        private fun getCalendarText(calendar: Calendar): String {
            return String.format(
                "农历%s",
                calendar.lunarCalendar.month.toString() + "月" + calendar.lunarCalendar.day + "日"
            )
        }
    }

    // 点击 checkbox 时的动作
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeTaskStatus (tag : String, view : CheckBox) {
        val status = if (view.isChecked) "done" else "todo"
        val newValue = ContentValues().apply {
            put ("task_status", status)
        }
        todolist_db.update("task", newValue, "task_tag = ?", arrayOf(tag))
        updateTaskListFromDB()
        taskThumbnailAdapter.updateTaskList(taskList)
    }
}