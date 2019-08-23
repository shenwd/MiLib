package com.shenwd.slibrary.srecycleview

import androidx.recyclerview.widget.RecyclerView


/**
 * RecycleView item 拖拽的监听接口
 */
interface OnItemDragListener {

    fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int)

    fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int)

    fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int)

}