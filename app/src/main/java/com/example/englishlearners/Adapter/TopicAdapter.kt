package com.example.englishlearners.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.englishlearners.Model.Topic
import com.example.englishlearners.R

class TopicAdapter(var context: Context, var topicList: ArrayList<Topic>)
    : BaseAdapter() {

    class ViewHolder(row: View) {
        var textViewTitle : TextView
        var image : ImageView

        init {
            textViewTitle = row.findViewById(R.id.text_view_topic_title)
            image = row.findViewById(R.id.image_topic)
        }
    }

    override fun getCount(): Int {
         return topicList.size
    }

    override fun getItem(position: Int): Any {
        return topicList.get(position)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder: ViewHolder
        if (convertView == null) {
            var layoutInflater : LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.listviewitem_topic, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }

        var topic: Topic = getItem(position) as Topic
        viewHolder.textViewTitle.text = topic.title
        return view as View 
    }
}