package com.bytedance.sjtu.liuyi.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sjtu.liuyi.Adapter.IdeaViewAdapter
import com.bytedance.sjtu.liuyi.DBHelper.IdeaItemDBHelper
import com.bytedance.sjtu.liuyi.R
import com.bytedance.sjtu.liuyi.databinding.ActivityScrollingBinding
import java.util.*

class IdeaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScrollingBinding
    lateinit var rvAdapter : IdeaViewAdapter
    private lateinit var rvIdea: RecyclerView
    lateinit var dateStr : String
    private lateinit var floatingCreationButton : com.google.android.material.floatingactionbutton.FloatingActionButton
    private val dbHelper = IdeaItemDBHelper(this, IDEA_DB_NAME, 1)
    private var db : SQLiteDatabase? = null
    private lateinit var all_idea_toolbar : androidx.appcompat.widget.Toolbar

    private val ideaItemRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            IdeaItemCreationActivity.IdeaItemCreationSuccessCode -> {
                rvAdapter.setIdeaList(dbHelper.getIdeaItemListByDate(dateStr))
            }
            IdeaItemCreationActivity.IdeaItemCreationFailCode -> {}
        }
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateStr = intent.getStringExtra("task_date").toString()
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindView()
        initBarView()
        setClickListener()
        bindDatabase()
        initRecyclerView()
    }

    private fun bindView() {
        all_idea_toolbar = findViewById(R.id.all_idea_toolbar)
        floatingCreationButton = findViewById(R.id.fab)
    }

    private fun setClickListener () {
        // 设置顶部按钮栏
        setSupportActionBar(all_idea_toolbar)
        all_idea_toolbar.setTitle("百草园")
        all_idea_toolbar.setNavigationIcon(R.drawable.to_left)
        setSupportActionBar(all_idea_toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        all_idea_toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "已返回", Toast.LENGTH_SHORT).show()
            finish()
        }
//        binding.toolbarLayout.title = title

        // 悬浮按钮
        binding.fab.setOnClickListener {
            intent = Intent(this, IdeaItemCreationActivity::class.java)
            ideaItemRequestLauncher.launch(intent)
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initBarView() {
        val tagFormatter = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = tagFormatter.format(Date())

        if (!formattedDate.equals(dateStr)) {
            floatingCreationButton.visibility = com.google.android.material.floatingactionbutton.FloatingActionButton.INVISIBLE
        }
    }

    private fun getParameterBundle(): Bundle? {
        return intent.extras
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindDatabase() {
        db = dbHelper.writableDatabase
    }



    private fun clearDatabaseItem() {
        dbHelper.deleteAllItem(db)
    }

    private fun initRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.idea_recycler_view)
        val layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        val adapter = IdeaViewAdapter(this)
        Log.d("IdeaActivity", "Query Date: $dateStr")
        adapter.setIdeaList(dbHelper.getIdeaItemListByDate(dateStr))
        rvAdapter = adapter
        rv.adapter = adapter
        rvIdea = rv
    }
}