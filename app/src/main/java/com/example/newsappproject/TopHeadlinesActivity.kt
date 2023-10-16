package com.example.newsappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LOG_TAG = "TOP HEADLINES PAGE"

class TopHeadlinesActivity : AppCompatActivity() {

    private lateinit var categoriesSpinner : Spinner
    private lateinit var topHeadlinesRecyclerView : RecyclerView
    private lateinit var backBtn : Button
    private lateinit var forwardBtn : Button
    private lateinit var pageNumberTextView : TextView
    private lateinit var progressBar : ProgressBar

    private var currentPageNumber = 1
    private var numOfPages = 0

    private var currentCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_headlines)

        // Init
        supportActionBar?.title = "Top Headlines"

        categoriesSpinner = findViewById(R.id.categoriesSpinner)
        topHeadlinesRecyclerView = findViewById(R.id.topHeadlinesRecyclerView)
        backBtn = findViewById(R.id.backButton)
        forwardBtn = findViewById(R.id.forwardButton)
        pageNumberTextView = findViewById(R.id.pageNumberTextView)
        progressBar = findViewById(R.id.progressBar)

        // Load initial sources list using first category
        initialise()

        // Spinner
        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = arrayAdapter
        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentCategory = parent.getItemAtPosition(position).toString()
                Log.d(LOG_TAG, "Spinner selected: $currentCategory")
                getTopHeadlines(currentCategory, 1)
                currentPageNumber = 1
                updatePageNumberText()
                updateButtonVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Button onClickListener
        forwardBtn.setOnClickListener {
            currentPageNumber++
            getTopHeadlines(currentCategory, currentPageNumber)
            updateButtonVisibility()
            pageNumberTextView.text = getString(R.string.page_number_text, currentPageNumber.toString(), numOfPages.toString())
        }

        backBtn.setOnClickListener {
            currentPageNumber--
            getTopHeadlines(currentCategory, currentPageNumber)
            updateButtonVisibility()
            pageNumberTextView.text = getString(R.string.page_number_text, currentPageNumber.toString(), numOfPages.toString())
        }
    }


    private fun initialise() {
        currentCategory = resources.getStringArray(R.array.categories)[0]
        getTopHeadlines(currentCategory, 1)
        updatePageNumberText()
    }


    private fun updateButtonVisibility() {
        forwardBtn.isVisible = currentPageNumber != numOfPages
        backBtn.isVisible = currentPageNumber != 1
    }


    private fun getTopHeadlines(category : String, pageNumber : Int) {
        Log.d(LOG_TAG, "API - getTopHeadlines($category)")

        progressBar.isVisible = true

        // Coroutines
        CoroutineScope(Dispatchers.IO).launch {
            val topHeadlines = ApiManager(this@TopHeadlinesActivity).getTopHeadlines(category, pageNumber)

            withContext(Dispatchers.Main) {
                // RecyclerView
                topHeadlinesRecyclerView.adapter = ArticlesAdapter(this@TopHeadlinesActivity, topHeadlines)
                topHeadlinesRecyclerView.layoutManager = LinearLayoutManager(this@TopHeadlinesActivity)
                progressBar.isVisible = false
            }
        }
    }


    private fun updatePageNumberText() {
        Log.d(LOG_TAG, "API - updatePageNumberText)")

        progressBar.isVisible = true

        // Coroutines
        CoroutineScope(Dispatchers.IO).launch {
            val numResults = ApiManager(this@TopHeadlinesActivity).getNumResults(currentCategory)
            Log.d(LOG_TAG, "Num of results = $numResults")

            withContext(Dispatchers.Main) {
                numOfPages = (numResults + 20 - 1) / 20
                Log.d(LOG_TAG, "Num of pages = $numOfPages")
                pageNumberTextView.text = getString(R.string.page_number_text, currentPageNumber.toString(), numOfPages.toString())
                progressBar.isVisible = false
            }
        }
    }
}