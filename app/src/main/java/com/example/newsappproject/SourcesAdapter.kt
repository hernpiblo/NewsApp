package com.example.newsappproject

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val LOG_TAG = "SOURCES ADAPTER"

class SourcesAdapter(private val appContext : Context, private val sources: List<Sources>, private val searchTerm : String) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {
    class ViewHolder(rootLayout: View): RecyclerView.ViewHolder(rootLayout) {
        val sourceName : TextView = rootLayout.findViewById(R.id.articleTitle)
        val sourceDescription: TextView = rootLayout.findViewById(R.id.articleSource)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(LOG_TAG, "inside onCreateViewHolder")
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.sources_card_layout, parent, false)
        return ViewHolder(rootLayout)
    }

    override fun getItemCount(): Int {
        return sources.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(LOG_TAG, "inside onBindViewHolder on position $position")
        val currentSource = sources[position]
        holder.sourceName.text = currentSource.name
        holder.sourceDescription.text = currentSource.description

        holder.itemView.setOnClickListener {
            Log.d(LOG_TAG, "Selected $currentSource")
            val searchResultIntent = Intent(appContext, SearchResultsActivity::class.java)
            searchResultIntent.putExtra("SearchTerm", searchTerm)
                              .putExtra("Source", currentSource.name)
                              .putExtra("SourceId", currentSource.sourceId)
            appContext.startActivity(searchResultIntent)
        }
    }
}