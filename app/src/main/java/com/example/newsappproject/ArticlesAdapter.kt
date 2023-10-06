package com.example.newsappproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


private const val LOG_TAG = "ARTICLES ADAPTER"

class ArticlesAdapter(private val appContext : Context, private val articles: List<Article>) : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {
    class ViewHolder(rootLayout: View): RecyclerView.ViewHolder(rootLayout) {
        val articleTitle : TextView = rootLayout.findViewById(R.id.articleTitle)
        val articleSource : TextView = rootLayout.findViewById(R.id.articleSource)
        val articleDescription : TextView = rootLayout.findViewById(R.id.articleDescription)
        val articleThumbnail : ImageView = rootLayout.findViewById(R.id.articleThumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(LOG_TAG, "inside onCreateViewHolder")
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.articles_card_layout, parent, false)
        return ViewHolder(rootLayout)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(LOG_TAG, "inside onBindViewHolder on position $position")
        val currentArticle = articles[position]
        holder.articleTitle.text = currentArticle.title.ifBlank { appContext.resources.getString(R.string.no_title) }
        holder.articleSource.text = currentArticle.source.ifBlank { appContext.resources.getString(R.string.no_source) }
        holder.articleDescription.text = currentArticle.description.ifBlank { appContext.resources.getString(R.string.no_description) }
//        holder.articleThumbnail.setImageBitmap(BitmapFactory.decodeStream(new URL(currentArticle.thumbnailUrl).openConnection().getInputStream()));

        holder.itemView.setOnClickListener {
            Log.d(LOG_TAG, "Selected $currentArticle")
            val url =
                if (!currentArticle.url.startsWith("http://") && !currentArticle.url.startsWith("https://")) {
                    "http://${currentArticle.url}"
                } else {
                    currentArticle.url
                }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            appContext.startActivity(intent)
            Toast.makeText(appContext, "Opening in browser: $url", Toast.LENGTH_LONG).show()
        }
    }
}