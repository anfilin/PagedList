package com.anfilin.android.pagedlist.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.anfilin.android.pagedlist.R
import com.anfilin.android.pagedlist.data.entity.HeaderItem
import com.anfilin.android.pagedlist.data.entity.SampleItem
import com.anfilin.android.pagedlist.databinding.FragmentMainBinding
import com.anfilin.android.pagedlist.viewmodel.SampleViewModel

/**
 * Main feed
 */
class MainFragment:
    Fragment(),
    SampleAdapterActionListener {

    private lateinit var viewDataBinding: FragmentMainBinding
    private lateinit var adapter: SampleAdapter

    /**
     * Create fragment view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        this.viewDataBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as AppCompatActivity).obtainViewModel(SampleViewModel::class.java)
        }

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        // Setup adapter
        this.adapter = SampleAdapter(this)
        viewDataBinding.recycler.adapter = adapter

        // Subscribe the adapter to the ViewModel, so the items in the adapter are refreshed
        // when the list changes
        viewDataBinding.viewmodel?.items?.observe(this, Observer { list ->
            adapter.submitList(list)
        })

        viewDataBinding.swipeContainer.setOnRefreshListener {
            viewDataBinding.viewmodel?.refresh()
        }
    }

    override fun onMessageClick(message: SampleItem) {
        Log.d("pagedlist", "onMessageClick")
    }

    companion object {

        private const val HEADER = 1
        private const val MESSAGE = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Any>() {

            override fun areItemsTheSame(old: Any, new: Any) =
                if (old is SampleItem && new is SampleItem) {
                    old.id == new.id
                } else {
                    false
                }

            override fun areContentsTheSame(old: Any, new: Any) =
                if (old is SampleItem && new is SampleItem) {
                    (old as SampleItem) == (new as SampleItem)
                } else {
                    false
                }
        }

    }

    /**
     * Adapter for messages
     */
    private inner class SampleAdapter internal constructor(
        val listener: SampleAdapterActionListener?) : PagedListAdapter<Any, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == HEADER) {
                return HeaderViewHolder(parent)
            }

            return MessageViewHolder(parent, listener)
        }

        override fun getItemViewType(position: Int): Int {
            if (getItem(position) is HeaderItem) {
                return HEADER
            }

            return MESSAGE
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is HeaderViewHolder) {
                holder.bindTo(getItem(position) as HeaderItem)
            }

            if (holder is MessageViewHolder) {
                holder.bindTo(getItem(position) as SampleItem)
            }
        }
    }

    /**
     * View holder for message
     */
    internal class MessageViewHolder(parent :ViewGroup,
                                    val listener: SampleAdapterActionListener?) : RecyclerView.ViewHolder(

        LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)) {

        private val messageLayout = itemView.findViewById<View>(R.id.message_layout)
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val message = itemView.findViewById<TextView>(R.id.message)

        /**
         * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
         * ViewHolder when Item is loaded.
         */
        fun bindTo(msg: SampleItem?) {

            if (msg == null) { return }

            title.text = msg.title
            message.text = msg.message

            messageLayout?.setOnClickListener {
                listener?.onMessageClick(msg)
            }
        }

    }

    /**
     * View holder for header
     */
    internal  class HeaderViewHolder(parent :ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)) {

        private val title = itemView.findViewById<TextView>(R.id.title)

        fun bindTo(msg: HeaderItem?) {
            title.text = msg?.message
        }
    }
}

interface SampleAdapterActionListener {
    fun onMessageClick(message: SampleItem)
}

