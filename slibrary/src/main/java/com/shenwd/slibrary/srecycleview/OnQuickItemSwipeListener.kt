package com.shenwd.slibrary.srecycleview

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView

/**
 * 简化接口
 */
abstract class OnQuickItemSwipeListener : OnItemSwipeListener {
    override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {
    }

    override fun onItemSwipeMoving(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, isCurrentlyActive: Boolean) {

    }

    override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {

    }

    override fun onSwipeToLefted(position: Int) {

    }

    override fun onSwipeToRighted(position: Int) {

    }

}