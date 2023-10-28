package com.company.chamberly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ChamberAdapter : BaseAdapter() {

    val dataList = mutableListOf<Chamber>()

    override fun getCount(): Int = dataList.size

    override fun getItem(position: Int): Chamber = dataList[position]

    override fun getItemId(position: Int): Long = position.toLong()


    fun setDataList(chambers: List<Chamber>) {
        dataList.clear()
        dataList.addAll(chambers)
        notifyDataSetChanged()
    }
    fun addData(chamber: Chamber) {
        dataList.add(chamber)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_koloda, parent, false)
        bindData(view, position)
        return view
    }


    private fun bindData(view: View, position: Int) {
        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val chamber = getItem(position)
        textTitle.text = "\"${chamber.groupTitle}\""
        // If you have other data fields and views, bind them here.
    }
}
