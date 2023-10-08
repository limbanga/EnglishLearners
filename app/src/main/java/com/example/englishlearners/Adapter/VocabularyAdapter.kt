package com.example.englishlearners.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.englishlearners.Model.Vocabulary
import com.example.englishlearners.R

class VocabularyAdapter(var context: Context, var vocabularyList: ArrayList<Vocabulary>)
    : BaseAdapter() {

    class ViewHolder(row: View) {
        var textViewTerm : TextView
        var textViewDefintion : TextView

        init {
            textViewTerm = row.findViewById(R.id.text_view_term)
            textViewDefintion = row.findViewById(R.id.text_view_definition)
        }
    }

    override fun getCount(): Int {
         return vocabularyList.size
    }

    override fun getItem(position: Int): Any {
        return vocabularyList[position]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder: ViewHolder
        if (convertView == null) {
            var layoutInflater : LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.list_view_item_vocabulary, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }


        var vocabulary: Vocabulary = getItem(position) as Vocabulary
        viewHolder.textViewTerm.text = vocabulary.term
        viewHolder.textViewDefintion.text = vocabulary.definition
        return view as View
    }
}