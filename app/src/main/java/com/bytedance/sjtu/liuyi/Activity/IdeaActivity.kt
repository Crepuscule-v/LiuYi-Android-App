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
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sjtu.liuyi.Adapter.IdeaViewAdapter
import com.bytedance.sjtu.liuyi.DataClass.IdeaItem
import com.bytedance.sjtu.liuyi.IdeaItemDBHelper
import com.bytedance.sjtu.liuyi.R
import com.bytedance.sjtu.liuyi.databinding.ActivityScrollingBinding
import java.util.*

class IdeaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var rvAdapter : IdeaViewAdapter
    private lateinit var rvIdea: RecyclerView
    private lateinit var dateStr : String
    private lateinit var floatingCreationButton : com.google.android.material.floatingactionbutton.FloatingActionButton
    private val dbHelper = IdeaItemDBHelper(this, "idea.db", 1)
    private var db : SQLiteDatabase? = null
    private var databaseTestItem : Boolean = false
    private var bundleTestItem : Boolean = false
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
//        setBundleTestItem(false)    // if set true, default date bundle will be set for the database filter.
//        inflateTestDateBundle()
        dateStr = intent.getStringExtra("task_date").toString()
//        setEnvironmentParameter()
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener {
            intent = Intent(this, IdeaItemCreationActivity::class.java)
            ideaItemRequestLauncher.launch(intent)
        }
        bindView()
        initBarView()
//        setDatabaseTestItem(false)    // if set true, default items will be inserted into database for testing.
        bindDatabase()
        initRecyclerView()
//        clearDatabaseItem()
    }

    private fun bindView() {
        floatingCreationButton = findViewById(R.id.fab)
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initBarView() {
        val tagFormatter = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = tagFormatter.format(Date())
//        Log.d("IdeaActivity", "formattedDate: $formattedDate, DateStr: $dateStr")

        if (!formattedDate.equals(dateStr)) {
//            Log.d("IdeaActivity", "hide button.")
            floatingCreationButton.visibility = com.google.android.material.floatingactionbutton.FloatingActionButton.INVISIBLE
        }
    }

    private fun getParameterBundle(): Bundle? {
        return intent.extras
    }

    private fun setEnvironmentParameter() {
//        val bundle = getParameterBundle()
//        val year = bundle!!.get("year")
//        val month = bundle.get("month").toString().toInt()
//        val day = bundle.get("day").toString().toInt()
//        dateStr = "$year-"
//        dateStr += if (month < 10) {
//            "0$month-"
//        } else {
//            "$month-"
//        }
//        dateStr += if (day < 10) {
//            "0$day"
//        } else {
//            "$day"
//        }
    }

    private fun inflateTestDateBundle() {
//        if (bundleTestItem) {
//            intent.putExtra("year", "2022")
//            intent.putExtra("month", "05")
//            intent.putExtra("day", "14")
//        }
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
        if (databaseTestItem) {
            initTestIdeaItem()
        }
    }

    private fun setBundleTestItem(b: Boolean) {
        bundleTestItem = b
    }

    private fun setDatabaseTestItem(b: Boolean) {
        databaseTestItem = b
    }

    private fun initTestIdeaItem() {
//        val testIdeaItem = IdeaItem(dateStr, "Keep simple, keep stupid.", "", "", "$dateStr-22-43-24")
//        val ideaList = dbHelper.getIdeaItemListByDate(dateStr)
//        if (ideaList.isEmpty()) {
//            dbHelper.insertIdeaItem(db, testIdeaItem)
//        }
    }

    private fun clearDatabaseItem() {
        dbHelper.deleteAllItem(db)
    }

    private fun initRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.idea_recycler_view)
        val layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        val adapter = IdeaViewAdapter()
        Log.d("IdeaActivity", "Query Date: $dateStr")
        adapter.setIdeaList(dbHelper.getIdeaItemListByDate(dateStr))
        rvAdapter = adapter
        rv.adapter = adapter
        rvIdea = rv
    }
}