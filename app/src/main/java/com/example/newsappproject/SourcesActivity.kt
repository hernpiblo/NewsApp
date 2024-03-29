package com.example.newsappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val LOG_TAG = "SOURCES PAGE"

class SourcesActivity : AppCompatActivity() {

    private lateinit var searchTermTextView : TextView
    private lateinit var categoriesSpinner : Spinner
    private lateinit var sourcesRecyclerView : RecyclerView
    private lateinit var skipBtn : Button
    private lateinit var progressBar : ProgressBar

    private lateinit var searchTerm : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)

        // Init
        supportActionBar?.title = "Sources"

        searchTermTextView = findViewById(R.id.searchTermTextView)
        categoriesSpinner = findViewById(R.id.categoriesSpinner)
        sourcesRecyclerView = findViewById(R.id.sourcesRecyclerView)
        skipBtn = findViewById(R.id.skipButton)
        progressBar = findViewById(R.id.progressBar)

        // Load initial sources list using first category
        getSources(resources.getStringArray(R.array.categories)[0])

        // Search Result View
        searchTerm = intent.getStringExtra("SearchTerm").toString()
        searchTermTextView.text = searchTerm

        // Spinner
        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = arrayAdapter
        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val category = parent.getItemAtPosition(position).toString()
                Log.d(LOG_TAG, "Spinner selected: $category")
                getSources(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Button
        skipBtn.setOnClickListener {
            Log.d(LOG_TAG, "BUTTON - skip button clicked")
            val searchResultIntent = Intent(this@SourcesActivity, SearchResultsActivity::class.java)
            searchResultIntent.putExtra("SearchTerm", searchTerm)
                              .putExtra("Source", "")
            startActivity(searchResultIntent)
        }
    }


    private fun getSources(category : String) {
        Log.d(LOG_TAG, "API - getSources($category)")

        progressBar.isVisible = true

        // Coroutines
        CoroutineScope(Dispatchers.IO).launch {
            val sources = ApiManager(this@SourcesActivity).getSources(category)
            withContext(Dispatchers.Main) {
                // RecyclerView
                sourcesRecyclerView.adapter = SourcesAdapter(this@SourcesActivity, sources, searchTerm)
                sourcesRecyclerView.layoutManager = LinearLayoutManager(this@SourcesActivity)

                progressBar.isVisible = false
            }
        }
    }
}