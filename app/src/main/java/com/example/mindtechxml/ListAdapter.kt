package com.example.mindtechxml

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(private var items: List<ListItem>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>(), Filterable {

    private var filteredItems: List<ListItem> = items


    fun updateItems(newItems: List<ListItem>) {
        items = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val titleView: TextView = view.findViewById(R.id.itemTitle)
        val subtitleView: TextView = view.findViewById(R.id.itemSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.titleView.text = item.title
        holder.subtitleView.text = item.subtitle
    }

    override fun getItemCount(): Int = filteredItems.size


    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    items
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    items.filter {
                        it.title.lowercase().contains(filterPattern)
                    }
                }
                Log.d("ListAdapter", "Filtered List: $filteredList")
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as List<ListItem>
                Log.d("ListAdapter", "Filtered Items: $filteredItems")
                notifyDataSetChanged()
            }
        }
    }

}

data class ListItem(val imageResId: Int, val title: String, val subtitle: String)
