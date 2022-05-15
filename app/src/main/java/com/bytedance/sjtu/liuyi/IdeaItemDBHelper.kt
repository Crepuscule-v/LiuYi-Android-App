package com.bytedance.sjtu.liuyi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.bytedance.sjtu.liuyi.DataClass.IdeaItem

class IdeaItemDBHelper (private val context: Context, fileName: String, version: Int): SQLiteOpenHelper(context, fileName, null, version) {
    private val createIdeaTable = "create table idea(" +
            "id integer primary key autoincrement," +
            "idea_date text," +
            "idea_text text," +
            "idea_image text," +
            "idea_video text," +
            "idea_tag text)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createIdeaTable)
    }

    fun insertIdeaItem(db: SQLiteDatabase?, ideaItem: IdeaItem) {
        val values = ContentValues().apply {
            put("idea_date", ideaItem.date)
            put("idea_text", ideaItem.text)
            put("idea_image", ideaItem.img)
            put("idea_video", ideaItem.video)
            put("idea_tag", ideaItem.tag)
        }
        db?.insert("idea", null, values)
    }

    fun deleteIdeaItem(db: SQLiteDatabase?, ideaItem: IdeaItem) {
        db?.delete("idea", "idea_tag = ?", arrayOf(ideaItem.tag))
    }

    fun deleteAllItem(db: SQLiteDatabase?) {
        db?.execSQL("delete from idea")
    }

    @SuppressLint("Range")
    fun getIdeaItemListByDate(date : String) : MutableList<IdeaItem> {
        val cursor = writableDatabase.query("idea", null, "idea_date = ?",
            arrayOf(date), null, null, null, null)
        val ideaItemList = mutableListOf<IdeaItem>()
        if (cursor.moveToFirst()) {
            do {
                ideaItemList.add(IdeaItem(
                    cursor.getString(cursor.getColumnIndex("idea_date")),
                    cursor.getString(cursor.getColumnIndex("idea_text")),
                    cursor.getString(cursor.getColumnIndex("idea_image")),
                    cursor.getString(cursor.getColumnIndex("idea_video")),
                    cursor.getString(cursor.getColumnIndex("idea_tag"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ideaItemList
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //
    }
}