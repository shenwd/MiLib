package com.shenwd.slibrary.srecycleview

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

/**
 * 简化接口
 */
abstract class OnQuickItemDragListener(val pressColor: Int, val endColor: Int) : OnItemDragListener {

    private var startIndex = -1
    private var endIndex = -1

    constructor() : this(0, 0)

    override fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int) {
        if (startIndex == -1) {
            startIndex = from
        }
        endIndex = to
    }

    override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
       pickUpAnimation(viewHolder.itemView)
    }

    override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {

        pickDownAnimation(viewHolder.itemView)

        if (startIndex != endIndex) onItemDrag(startIndex, endIndex)
        startIndex = -1
        endIndex = -1
    }

    abstract fun onItemDrag(startIndex: Int, endIndex: Int)

    /**
     * 使用 Z轴 平移来实现 上浮动效果
     */
    private fun pickUpAnimation(view: View?) {
        val animator = ObjectAnimator.ofFloat(view, "translationZ", 0f, 20f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 300
        animator.start()
    }

    /**
     * 使用 Z轴 平移来实现 上浮动效果
     */
    private fun pickDownAnimation(view: View?) {
        val animator = ObjectAnimator.ofFloat(view, "translationZ", 20f, 0f)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 300
        animator.start()
    }
}