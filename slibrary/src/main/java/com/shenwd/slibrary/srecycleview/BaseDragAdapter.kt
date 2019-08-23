package com.shenwd.slibrary.srecycleview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.util.*

/**
 * 拖拽 adapter
 */
abstract class BaseDragAdapter<T>(context : Context?, private var data: MutableList<T>) :
    BaseAdapter<T>(context, data), ItemDragCallBack.OnItemDragLinstener {

    private lateinit var onItemDragListener: OnItemDragListener
    private lateinit var onItemSwipeListener: OnItemSwipeListener

    override fun onSwipedToRight(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (!this::onItemSwipeListener.isInitialized) {
            throw Exception("onItemSwipeListener not initialized")
        }
        if (isSwipeRightDelete) {
            data.removeAt(position)
            notifyItemRemoved(viewHolder.adapterPosition)
            onItemSwipeListener.onSwipeToRighted(viewHolder.adapterPosition)
        } else {
            onItemSwipeListener.onSwipeToRighted(position)
            //侧滑不删除的情况下，需要notifyDataChanged来修正view
            notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    override fun onSwipedToLeft(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (isSwipeLeftDelete) {
            data.removeAt(position)
            notifyItemRemoved(position)
            onItemSwipeListener.onSwipeToLefted(position)
        } else {
            onItemSwipeListener.onSwipeToLefted(position)
            //侧滑不删除的情况下，需要notifyDataChanged来修正view
            notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    /**
     * 设置向右侧滑 是否删除
     */
    private var isSwipeRightDelete = true

    fun isSwipeRightDelete(delete: Boolean) {
        if (this::itemDragCallBack.isInitialized) itemDragCallBack.setSwipeOverViewBack(!delete)
        this.isSwipeRightDelete = delete
    }

    /**
     * 设置向左侧滑 是否删除
     */
    private var isSwipeLeftDelete = true

    fun isSwipeLeftDelete(delete: Boolean) {
        this.isSwipeLeftDelete = delete
    }


    override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        onItemSwipeListener.onItemSwipeStart(viewHolder, pos)
    }

    override fun onItemSwipeMoving(
        canvas: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        isCurrentlyActive: Boolean
    ) {
        onItemSwipeListener.onItemSwipeMoving(canvas, viewHolder, dX, dY, isCurrentlyActive)
    }

    override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        onItemSwipeListener.clearView(viewHolder, pos)
    }

    override fun onItemMoveStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        onItemDragListener.onItemDragStart(viewHolder, pos)
    }

    override fun onItemDragMoving(
        source: RecyclerView.ViewHolder,
        from: Int,
        target: RecyclerView.ViewHolder,
        to: Int
    ) {
        Collections.swap(data, from, to)
        notifyItemMoved(source.adapterPosition, target.adapterPosition)
        onItemDragListener.onItemDragMoving(source, from, target, to)
    }

    override fun onItemMoveEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
        onItemDragListener.onItemDragEnd(viewHolder, pos)
    }

    override fun getHeaderViewSize(): Int {
        return getHeaderViewSize()
    }

    fun setOnItemDragListener(onItemDragListener: OnItemDragListener) {
        this.onItemDragListener = onItemDragListener
    }

    fun setOnItemSwipeListener(onItemSwipeListener: OnItemSwipeListener) {
        this.onItemSwipeListener = onItemSwipeListener
    }


    /**
     * 拖拽 和 侧滑 的callback
     */
    private lateinit var itemDragCallBack: ItemDragCallBack

    fun newItemDragCallBack(): ItemDragCallBack {
        itemDragCallBack = ItemDragCallBack()
        itemDragCallBack.setOnItemDragListener(this)
        return itemDragCallBack
    }

    fun getItemDragCallBack() = itemDragCallBack

    override fun getSwipeRightBitmap(): Bitmap? = null

    override fun getSwipeLeftBitmap(): Bitmap? = null

}