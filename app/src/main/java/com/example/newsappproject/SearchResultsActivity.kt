package com.example.newsappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LOG_TAG = "SEARCH RESULTS PAGE"

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var sourceTextView: TextView
    private lateinit var searchTermTextView : TextView
    private lateinit var articlesRecyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar

    private lateinit var source : String
    private lateinit var sourceId : String
    private lateinit var searchTerm : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        // Init
        supportActionBar?.title = "Search results"

        sourceTextView = findViewById(R.id.sourceTextView)
        searchTermTextView = findViewById(R.id.searchTermTextView)
        articlesRecyclerView = findViewById(R.id.articlesRecyclerView)
        progressBar = findViewById(R.id.progressBar)

        source = intent.getStringExtra("Source").toString()
//        sourceTextView.text = getString(R.string.source_news_for, source).ifBlank { getString(R.string.news_for)}
        sourceTextView.text = if (source.isNotBlank()) {
            getString(R.string.source_news_for, source)
        } else {
            getString(R.string.news_for)
        }

        sourceId = intent.getStringExtra("SourceId").toString()

        searchTerm = intent.getStringExtra("SearchTerm").toString()
        searchTermTextView.text = searchTerm

        getArticles(searchTerm, sourceId)

    }

    private fun getArticles(searchTerm: String, sourceId : String) {

        Log.d(LOG_TAG, "API - getArticles($searchTerm, $sourceId)")

        progressBar.isVisible = true

        // Coroutines
        CoroutineScope(Dispatchers.IO).launch {
            val articles = ApiManager(this@SearchResultsActivity).getArticles(searchTerm, sourceId)

            withContext(Dispatchers.Main) {
                if (articles.isEmpty()) {
                    articlesRecyclerView.isVisible = false
                } else {
                    // RecyclerView
                    articlesRecyclerView.adapter = ArticlesAdapter(this@SearchResultsActivity, articles)
                    articlesRecyclerView.layoutManager = LinearLayoutManager(this@SearchResultsActivity)
                }
                progressBar.isVisible = false
            }
        }
    }
}