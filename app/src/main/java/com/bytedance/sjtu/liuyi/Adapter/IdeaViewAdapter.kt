package com.bytedance.sjtu.liuyi.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sjtu.liuyi.DataClass.IdeaItem
import com.bytedance.sjtu.liuyi.R

class IdeaViewAdapter: RecyclerView.Adapter<IdeaViewAdapter.IdeaViewHolder>() {
    private var ideaList : MutableList<IdeaItem> = arrayListOf()
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
            ideaItemTimeView.text = tag2Time(ideaItem.tag)
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
        return IdeaViewHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        holder.bindView(ideaList[position])
    }

    override fun getItemCount(): Int {
        return ideaList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setIdeaList(newList: MutableList<IdeaItem>) {
        ideaList.clear()
        ideaList.addAll(newList)
        notifyDataSetChanged()
    }
}