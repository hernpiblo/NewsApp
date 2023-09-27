package com.example.newsappproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SourcesAdapter(private val sources: List<Sources>) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {
    class ViewHolder(rootLayout: View): RecyclerView.ViewHolder(rootLayout) {
        val sourceName : TextView = rootLayout.findViewById(R.id.sourceName)
        val sourceDescription: TextView = rootLayout.findViewById(R.id.sourceDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("VH", "inside onCreateViewHolder")
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.sourcescardlayout, parent, false)
        return ViewHolder(rootLayout)
    }

    override fun getItemCount(): Int {
        return sources.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("VH", "inside onBindViewHolder on position $position")
        val currentSource = sources[position]
        holder.sourceName.text = currentSource.name
        holder.sourceDescription.text = currentSource.description

        holder.itemView.setOnClickListener {
            Log.d("SOURCE ITEM", "Selected $currentSource")
        }
    }
}