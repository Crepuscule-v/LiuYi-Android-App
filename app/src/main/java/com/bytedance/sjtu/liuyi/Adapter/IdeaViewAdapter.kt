package com.bytedance.sjtu.liuyi.Adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sjtu.liuyi.Activity.IDEA_DB_NAME
import com.bytedance.sjtu.liuyi.Activity.IdeaActivity
import com.bytedance.sjtu.liuyi.Activity.TAG_PATTERN
import com.bytedance.sjtu.liuyi.DataClass.IdeaItem
import com.bytedance.sjtu.liuyi.DBHelper.IdeaItemDBHelper
import com.bytedance.sjtu.liuyi.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

class IdeaViewAdapter (activity : IdeaActivity): RecyclerView.Adapter<IdeaViewAdapter.IdeaViewHolder>() {
    private var ideaList : MutableList<IdeaItem> = arrayListOf()
    private var idea_activity : IdeaActivity = activity
    inner class IdeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ideaItemTextView: TextView = itemView.findViewById(R.id.tv_idea_item_text)
        private val ideaItemImageView: ImageView = itemView.findViewById(R.id.iv_idea_item_image)
        private val ideaItemTimeView : TextView = itemView.findViewById(R.id.tv_idea_item_time)

        fun bindView(ideaItem : IdeaItem) {
            if (ideaItem.text.isBlank()) {
                ideaItemTextView.visibility = View.GONE
            }
            else {
                ideaItemTextView.text = ideaItem.text
                ideaItemTextView.visibility = View.VISIBLE
            }
            ideaItemTimeView.text = ideaItem.tag                // 设置为 yyyy-MM-dd-hh-mm-ss
            if (ideaItem.img.isBlank()) {
                ideaItemImageView.visibility = View.GONE
            }
            else {
                ideaItemImageView.setImageURI(Uri.parse(ideaItem.img))
                ideaItemImageView.visibility = View.VISIBLE
            }
        }

        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            /**
             * todo calculate sampleSize
             */
            return 2
        }

        private fun decodeBitmapFromFile(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(path, options)
        }

        private fun tag2Time(tag : String) : String {
            val timeList = tag.split('-')
            return "${timeList[3]}:${timeList[4]}:${timeList[5]}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.idea_item_view, parent, false)
        val delete_idea_entry_btn = view.findViewById<ImageButton>(R.id.delete_idea_entry_btn)
        val dbHelper = IdeaItemDBHelper(idea_activity, IDEA_DB_NAME)
        val db = dbHelper.writableDatabase
        val ideaViewHolder = IdeaViewHolder(view)
        val ideaTimeView = view.findViewById<TextView>(R.id.tv_idea_item_time)

        // 删除按钮点击事件
        delete_idea_entry_btn.setOnClickListener {
            db.delete("idea", "idea_tag = ?", arrayOf(ideaTimeView.text.toString()))
            setIdeaList(dbHelper.getIdeaItemListByDate(idea_activity.dateStr))
        }

        return ideaViewHolder
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        holder.bindView(ideaList[position])
    }

    override fun getItemCount(): Int {
        return ideaList.size
    }

    // 按照创建先后排序，新发布的排最前面
    @SuppressLint("SimpleDateFormat")
    val myComparator : Comparator<IdeaItem> = Comparator { idea_1, idea_2 ->
        val dateformatter: SimpleDateFormat = SimpleDateFormat(TAG_PATTERN)
        val date_1: Date = dateformatter.parse(idea_1.tag)
        val date_2: Date = dateformatter.parse(idea_2.tag)
        if (date_1.before(date_2)) return@Comparator 1
        return@Comparator -1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setIdeaList(newList: MutableList<IdeaItem>) {
        ideaList.clear()
        ideaList.addAll(newList)
        ideaList.sortWith(myComparator)
        notifyDataSetChanged()
    }
}