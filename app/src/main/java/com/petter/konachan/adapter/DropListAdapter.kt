package com.petter.konachan.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

/**
 * @anthor: EDZ
 * @time: 2021/11/16 10:59
 * @description:
 */
class DropListAdapter(val context: Context, val data: MutableList<String>): BaseAdapter(), Filterable {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView: TextView = if (convertView == null) {
            LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false) as TextView
        }else{
            convertView as TextView
        }
        textView.text = data[position]
        return textView
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
//                val mutableListOf = mutableListOf<String>()
//                if (constraint != null) {
//                    for (datum in data) {
//                        if (datum.contains(constraint)) {
//                            mutableListOf.add(datum)
//                        }
//                    }
//                }
                filterResults.values = data
                filterResults.count = data.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                data.clear()
//                results?.let {
//                    data.addAll(it.values as Collection<String>)
//                }
                notifyDataSetChanged()
            }
        }
    }
}