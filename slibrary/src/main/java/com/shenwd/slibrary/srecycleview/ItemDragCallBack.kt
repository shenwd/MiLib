package com.shenwd.slibrary.srecycleview

import android.animation.ValueAnimator
import android.graphics.*
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class ItemDragCallBack : ItemTouchHelper.Callback() {

    // 总开关
    private var isDrag = true
    private var isSwipeLeft = true
    private var isSwipeRight = true


    //拖动状态
    private var isDraging = false
    private var isSwiping = false

    /**
     *     设置滑动类型标记
     *
     *     如果是列表类型的，拖拽只有ItemTouchHelper.UP、ItemTouchHelper.DOWN两个方向
     *     如果是网格类型的，拖拽则有UP、DOWN、LEFT、RIGHT四个方向
     *
     *     另外，滑动方向列表类型的，有START和END两个方法，如果是网格类型的一般不设置支持滑动操作可以将swipeFlags = 0置为0，表示不支持滑动操作！
     *     最后，需要调用return makeMovementFlags(dragFlags, swipeFlags)将设置的标志位return回去！
     *
     * @param recyclerView
     * @param viewHolder
     * @return 返回一个整数类型的标识，用于判断Item那种移动行为是允许的
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags = 0
        if (isDrag && !isNotDragView(viewHolder)) {
            if (recyclerView.layoutManager is GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            } else if (recyclerView.layoutManager is LinearLayoutManager) {
                dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//                val ll = recyclerView.layoutManager as LinearLayoutManager
            }
        }
        var swipeFlags = 0
        if (!isNotDragView(viewHolder)) {
            if (isSwipeLeft && isSwipeRight) {
                swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            } else if (isSwipeLeft) {
                swipeFlags = ItemTouchHelper.START
            } else if (isSwipeRight) {
                swipeFlags = ItemTouchHelper.END
            }
        }

        return makeMovementFlags(dragFlags, swipeFlags)

    }

    /**
     * 拖拽切换Item的回调
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return 如果Item切换了位置，返回true；反之，返回false
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemViewType == target.itemViewType
    }

    override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        val fromPosition = viewHolder.adapterPosition - mDragLinstener.getHeaderViewSize()
        val toPosition = target.adapterPosition - mDragLinstener.getHeaderViewSize()

        mDragLinstener.onItemDragMoving(viewHolder, fromPosition, target, toPosition)
    }

    /**
     * 滑动Item
     *
     * @param viewHolder
     * @param direction Item滑动的方向
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == 16) {//向左滑动
            mDragLinstener.onSwipedToLeft(viewHolder, viewHolder.adapterPosition - mDragLinstener.getHeaderViewSize())
        } else if (direction == 32) {//向右滑动
            mDragLinstener.onSwipedToRight(viewHolder, viewHolder.adapterPosition - mDragLinstener.getHeaderViewSize())
        }
        isSwiping = false
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        // 设置侧滑后 恢复原始view的效果
        if (!isCurrentlyActive && isSwipeOverBack && isSwiping) {
            viewHolder?.let {
                val oa = ValueAnimator.ofFloat(it.itemView.x, 0f)
                oa.duration = 300
                oa.addUpdateListener { animation ->
                    it.itemView.translationX = animation.animatedValue as Float

                }
                oa.start()
            }
        }
    }

    /**
     * Item被选中时候回调
     *
     * @param viewHolder
     * @param actionState
     *          当前Item的状态
     *          ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
     *          ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
     *          ItemTouchHelper#ACTION_STATE_DRAG   拖拽中状态
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if(this::mDragLinstener.isInitialized){
            throw Exception("mDragLinstener not initialized")
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            Log.d("actionState", "闲置状态")
        }
        viewHolder?.let {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && !isNotDragView(viewHolder)) {
                mDragLinstener.onItemMoveStart(it, it.adapterPosition.minus(mDragLinstener.getHeaderViewSize()))
                isDraging = true
                Log.d("actionState", "拖动状态")
            }
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isNotDragView(viewHolder)) {
                Log.d("actionState", "滑动状态")
                mDragLinstener.onItemSwipeStart(it, it.adapterPosition.minus(mDragLinstener.getHeaderViewSize()))
                isSwiping = true
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if(this::mDragLinstener.isInitialized){
            throw Exception("mDragLinstener not initialized")
        }
        if (isNotDragView(viewHolder)) {
            return
        }
        if (isDraging) {
            mDragLinstener.onItemMoveEnd(viewHolder, viewHolder.adapterPosition - mDragLinstener.getHeaderViewSize())
            isDraging = false
        }
        if (isSwiping) {
            mDragLinstener.clearView(viewHolder, viewHolder.adapterPosition - mDragLinstener.getHeaderViewSize())
        }
    }

    /**
     * 判断当前itemview 是否应该允许 滑动
     *
     *
     */
    private fun isNotDragView(viewHolder: RecyclerView.ViewHolder?): Boolean {
        val type = viewHolder?.itemViewType
//        return (type == BaseQuickAdapter.HEADER_VIEW || type == BaseQuickAdapter.LOADING_VIEW
//                || type == BaseQuickAdapter.FOOTER_VIEW || type == BaseQuickAdapter.EMPTY_VIEW)
        return type != BaseAdapter.NORMAL_VIEW
    }

//    /**
//     * Item 是否支持长按拖动
//     * 默认支持
//     */
//    override fun isLongPressDragEnabled(): Boolean {
//        return super.isLongPressDragEnabled()
//    }

//    /**
//     * Item 是否支持滑动,默认为true
//     */
//    override fun isItemViewSwipeEnabled(): Boolean {
//        return super.isItemViewSwipeEnabled()
//    }

    /**
     * 移动过程中绘制Item
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     *          X轴移动的距离
     * @param dY
     *          Y轴移动的距离
     * @param actionState
     *          当前Item的状态
     * @param isCurrentlyActive
     *          如果当前被用户操作为true，反之为false
     */
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        if(this::mDragLinstener.isInitialized){
            throw Exception("mDragLinstener not initialized")
        }

        Log.d(this::class.simpleName, "actionState:$actionState:$isCurrentlyActive")
        //滑动时自己实现背景及图片
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            //dX大于0时向右滑动，小于0向左滑动
            val itemView = viewHolder.itemView//获取滑动的view
            val padding = 50//图片绘制的padding

            val swipeLeftBitmap = mDragLinstener.getSwipeLeftBitmap()//获取向左滑动指示的背景图片
            val swipeRightBitmap = mDragLinstener.getSwipeRightBitmap()//获取向右滑动指示的背景图片
//            val maxDrawWidth = 2 * padding + swipeLeftBitmap.getWidth()//最大的绘制宽度

            val width = abs(dX).roundToInt() + 1//滑动的宽度
            val itemTop = itemView.bottom - itemView.height//绘制的top位置

            val bgPaint = Paint()//背景的paint
            bgPaint.color = Color.parseColor("#e5e5e0")

            swipeRightBitmap?.let {
                if(dX > 0){
                    //在背景上面绘制图片
                    //根据滑动实时绘制一个背景
                    c.drawRect(itemView.left.toFloat(), itemTop.toFloat(), width.toFloat(), itemView.bottom.toFloat(), bgPaint)

                    //指定图片绘制的位置
                    val rect = Rect()//画图的位置
                    rect.left = itemView.left + padding

                    rect.top = itemTop + (itemView.bottom - itemTop - it.height) / 2//图片居中
                    val maxRight = rect.left + it.width
                    rect.right = min(width, maxRight)
                    rect.bottom = rect.top + it.height
                    //指定图片的绘制区域
                    var rect1 = Rect()
                    if (width < itemView.width) {
                        rect1 = Rect()//不能再外面初始化，否则dx大于画图区域时，删除图片不显示
                        rect1.left = 0
                        rect1.top = 0
                        rect1.bottom = it.height
                        rect1.right = width - padding
                    }

                    val bgBitmap = Bitmap.createBitmap(width, itemView.height, Bitmap.Config.RGB_565)
                    val ca = Canvas(bgBitmap)
                    val p = Paint()
                    p.color = Color.parseColor("#1296db")
                    if (dX > itemView.width / 2) {
                        ca.drawCircle((padding + it.width / 2).toFloat(), itemView.height / 2.toFloat(), (dX - (itemView.width / 2)) * 2, p)
                        ca.save()
                    }

                    c.drawBitmap(bgBitmap, 0f, itemTop.toFloat(), p)
                    c.drawBitmap(it, rect1, rect, bgPaint)

                    if (dX > itemView.width) {
                        bgBitmap.recycle()
//                    swipeRightBitmap.recycle()
                    }
                    //绘制时需调用平移动画，否则滑动看不到反馈
                    itemView.translationX = dX
                }
            }

            if(dX < 0){
                swipeLeftBitmap?.let {
                    //在背景上面绘制图片
                    //根据滑动实时绘制一个背景
                    c.drawRect((itemView.width - width).toFloat(), itemTop.toFloat(), itemView.width.toFloat(), itemView.bottom.toFloat(), bgPaint)

                    //指定图片绘制的位置
                    val dst = Rect()//画图的位置
                    dst.left = itemView.width - it.width - padding
                    dst.top = itemTop + (itemView.bottom - itemTop - it.height) / 2//图片居中
                    val maxRight = dst.left + it.width
                    dst.right = maxRight
                    dst.bottom = dst.top + it.height
                    //指定图片的绘制区域
                    var src = Rect()
                    if (width < itemView.width) {
                        src = Rect()//不能再外面初始化，否则dx大于画图区域时，删除图片不显示
                        src.left = 0
                        src.top = 0
                        src.bottom = it.height
                        src.right = it.width
                    }

                    val bgBitmap = Bitmap.createBitmap(width, itemView.height, Bitmap.Config.RGB_565)
                    val ca = Canvas(bgBitmap)
                    val p = Paint()
                    p.color = Color.parseColor("#0000ff")
                    if (width > itemView.width / 2) {
                        ca.drawCircle((bgBitmap.width - padding - it.width / 2).toFloat(), itemView.height / 2.toFloat(), ((width - (itemView.width / 2)) * 2).toFloat(), p)
                        ca.save()
                    }

                    c.drawBitmap(bgBitmap, (itemView.width - width).toFloat(), itemTop.toFloat(), p)
                    c.drawBitmap(it, src, dst, bgPaint)

                    if (width > itemView.width) {
//                    bgBitmap.recycle()
//                    swipeLeftBitmap.recycle()
                    }
                }
            }
            //绘制时需调用平移动画，否则滑动看不到反馈
            itemView.translationX = dX
            mDragLinstener.onItemSwipeMoving(c, viewHolder, dX, dY, isCurrentlyActive)
        } else {
            //拖动时有系统自己完成
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }


    fun enableDragAndSwipe(isDrag: Boolean, isSwipeLeft: Boolean, isSwipeRight: Boolean) {
        this.isDrag = isDrag
        this.isSwipeLeft = isSwipeLeft
        this.isSwipeRight = isSwipeRight

    }

    private lateinit var mDragLinstener: OnItemDragLinstener

    interface OnItemDragLinstener {

        fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int)
        fun onItemSwipeMoving(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, isCurrentlyActive: Boolean)
        fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int)
        //Swipe 结束
        fun onSwipedToRight(viewHolder: RecyclerView.ViewHolder, position: Int)

        fun onSwipedToLeft(viewHolder: RecyclerView.ViewHolder, position: Int)

        fun onItemMoveStart(viewHolder: RecyclerView.ViewHolder, pos: Int)
        fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int)
        fun onItemMoveEnd(viewHolder: RecyclerView.ViewHolder, pos: Int)

        fun getSwipeLeftBitmap(): Bitmap?
        fun getSwipeRightBitmap(): Bitmap?
        fun getHeaderViewSize(): Int

    }

    fun setOnItemDragListener(mDragLinstener: OnItemDragLinstener) {
        this.mDragLinstener = mDragLinstener
    }

    /**
     * 设置侧滑后 view 返回原始效果 即不需要删除
     */
    private var isSwipeOverBack = false

    fun setSwipeOverViewBack(back: Boolean) {
        isSwipeOverBack = back
    }
}