package com.example.gamecollection.ui

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecollection.R
import com.example.gamecollection.data.Store

class StoresAdapter (private val onStoreClick:(Store) -> Unit)
    :RecyclerView.Adapter<StoresAdapter.ViewHolder>(){
    private var stores = listOf<Store?>()

    fun updateStoreList(newStoreList: List<Store?>) {
        stores = newStoreList
        notifyDataSetChanged()
    }

    override fun getItemCount() = this.stores.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_item,parent,false)
        return ViewHolder(itemView, onStoreClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        this.stores[position]?.let { holder.bind(it) }
    }

    class ViewHolder(itemView: View, private val onStoreClick: (Store) -> Unit)
        :RecyclerView.ViewHolder(itemView){
        private val nameTV : TextView = itemView.findViewById(R.id.tv_store_name)
        private var currentStore: Store ?= null

        init {
            itemView.setOnClickListener{
                currentStore?.let(onStoreClick)
            }
        }
        fun bind(stores: Store){
            currentStore = stores
            val store = SpannableStringBuilder(stores.store.name)
            store.setSpan(UnderlineSpan(), 0, store.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            nameTV.text = store
        }
    }
}