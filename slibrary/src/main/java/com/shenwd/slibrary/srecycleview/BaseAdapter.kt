package com.shenwd.slibrary.srecycleview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 顶层 adapter
 */
abstract class BaseAdapter<T>(private val context: Context?, private val data: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val footViews = arrayListOf<View>()
    private val headerViews = arrayListOf<View>()

    companion object {
        const val HEADER_VIEW_TYPER = 10000
        const val FOOT_VIEW_TYPE = 20000
        const val NORMAL_VIEW = -1
    }

    // itemview 点击事件
    private lateinit var onItemClick: (RecyclerView.ViewHolder, Int) -> Unit

    fun setOnItemClickListener(onItemClick: (RecyclerView.ViewHolder, Int) -> Unit) {
        this.onItemClick = onItemClick
    }

    // itemview 长点击事件
    private lateinit var onItemLongClick: (RecyclerView.ViewHolder, Int) -> Unit

    fun setOnItemLongClickListener(onItemLongClick: (RecyclerView.ViewHolder, Int) -> Unit) {
        this.onItemLongClick = onItemLongClick
    }

    // 子view 的 点击事件
    private lateinit var onItemChildClick: (View) -> Unit

    fun setOnItemChildClickListener(onItemChildClick: (View) -> Unit) {
        this.onItemChildClick = onItemChildClick
    }

    private fun addChildClickListener(view: View) {
        if (this::onItemChildClick.isInitialized) {
            view.setOnClickListener {
                onItemChildClick.invoke(view)
            }
        }
    }

    // 子view 的 长点击事件
    private lateinit var onItemLongChildClick: (View) -> Unit

    fun setOnItemLongChildClickListener(onItemLongChildClick: (View) -> Unit) {
        this.onItemLongChildClick = onItemLongChildClick
    }

    private fun addLongChildClickListener(view: View) {
        if (this::onItemLongChildClick.isInitialized) {
            view.setOnLongClickListener {
                onItemLongChildClick.invoke(view)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType in HEADER_VIEW_TYPER until FOOT_VIEW_TYPE) {
            return HeaderViewHolder(headerViews[viewType - HEADER_VIEW_TYPER])
        }
        if (viewType >= FOOT_VIEW_TYPE) {
            return FootrViewHolder(footViews[viewType - FOOT_VIEW_TYPE])
        }

        if (viewType == NORMAL_VIEW) {
            return createCustomViewHolder(parent)
        }
        return super.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder.adapterPosition >= headerViews.size && viewHolder.adapterPosition < (headerViews.size + data.size)) {
            if (this::onItemClick.isInitialized) {
                viewHolder.itemView.setOnClickListener {
                    onItemClick.invoke(viewHolder, viewHolder.adapterPosition - headerViews.size)
                }
            }
            if (this::onItemLongChildClick.isInitialized) {
                viewHolder.itemView.setOnLongClickListener {
                    onItemLongClick.invoke(viewHolder, viewHolder.adapterPosition - headerViews.size)
                    true
                }
            }
            onBindCustomViewHolder(viewHolder, data[viewHolder.adapterPosition - headerViews.size])
        }
    }


    abstract fun onBindCustomViewHolder(viewHolder: RecyclerView.ViewHolder, t: T)


    /**
     * 默认的 viewholder
     *
     * 自定义Holder 可以覆盖此方法
     */
    fun createCustomViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return InnerHolder(LayoutInflater.from(context).inflate(createViewById(), parent, false))
    }

    abstract fun createViewById(): Int

    override fun getItemCount() = footViews.size + data.size + headerViews.size

    override fun getItemViewType(position: Int): Int {
        if (headerViews.size != 0 && position < headerViews.size) {
            return HEADER_VIEW_TYPER + position
        }
        if (footViews.size != 0 && position > (headerViews.size + data.size - 1)) {
            return FOOT_VIEW_TYPE + (position - headerViews.size - data.size)
        }
        return NORMAL_VIEW
    }

    fun addHeaderView(view: View) {
        if (headerViews.size > 999) {
            return
        }
        headerViews.add(view)
        val indexOf = headerViews.indexOf(view)
        notifyItemInserted(indexOf)
    }

    fun addFootView(view: View) {
        if (footViews.size > 999) {
            return
        }
        footViews.add(view)
        val indexOf = footViews.indexOf(view)
        notifyItemInserted(headerViews.size + data.size + indexOf)
    }

    fun removeHeaderView(view: View) {
        val indexOf = headerViews.indexOf(view)
        headerViews.remove(view)
        notifyItemRemoved(indexOf)
    }

    fun removeFootView(view: View) {
        val indexOf = footViews.indexOf(view)
        footViews.remove(view)
        notifyItemRemoved(headerViews.size + data.size + indexOf)
    }


    class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view)

    class FootrViewHolder(private val view: View) : RecyclerView.ViewHolder(view)

    class InnerHolder(private val view: View) : RecyclerView.ViewHolder(view)

}