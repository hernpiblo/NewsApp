package com.example.newsappproject

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SourcesActivity : AppCompatActivity() {

    private lateinit var searchTermTextView : TextView
    private lateinit var categoriesSpinner : Spinner
    private lateinit var sourcesRecyclerView : RecyclerView
    private lateinit var skipBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)

        // Init
        searchTermTextView = findViewById(R.id.searchTermTextView)
        categoriesSpinner = findViewById(R.id.categoriesSpinner)
        sourcesRecyclerView = findViewById(R.id.sourcesRecyclerView)
        skipBtn = findViewById(R.id.skipButton)

        // Search Result View
        val searchTerm : String = intent.getStringExtra("SearchTerm").toString()
        searchTermTextView.text = searchTerm

        // Spinner
        val arrayAdapter : ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            getCategories()
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = arrayAdapter


        // RecyclerView
        val sources = getSources()
        sourcesRecyclerView.adapter = SourcesAdapter(this, sources)
        sourcesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Button
        skipBtn.setOnClickListener {
            Log.d("SOURCES PAGE", "BUTTON - skip button clicked")
        }

    }

    private fun getSources() : List<Sources> {
        Log.d("SOURCES PAGE", "API - getSources()")
        return listOf(
            Sources("ABC News (AU)",  "Australia's most trusted source of local, national and world news. Comprehensive, independent, in-depth analysis, the latest business, sport, weather and more."),
            Sources("Ars Technica",  "The PC enthusiast's resource. Power users and the tools they love, without computing religion."),
            Sources("Associated Press",  "The AP delivers in-depth coverage on the international, politics, lifestyle, business, and entertainment news."),
            Sources("BBC News",  "Use BBC News for up-to-the-minute news, breaking news, video, audio and feature stories. BBC News provides trusted World and UK news as well as local and regional perspectives. Also entertainment, business, science, technology and health news."),
            Sources("Bleacher Report",  "Sports journalists and bloggers covering NFL, MLB, NBA, NHL, MMA, college football and basketball, NASCAR, fantasy sports and more. News, photos, mock drafts, game scores, player profiles and more!"),
            Sources("Business Insider",  "Business Insider is a fast-growing business site with deep financial, media, tech, and other industry verticals. Launched in 2007, the site is now the largest business news site on the web."),
            Sources("CNN",  "View the latest news and breaking news today for U.S., world, weather, entertainment, politics and health at CNN"),
        )
    }

    private fun getCategories() : List<String> {
        Log.d("SOURCES PAGE", "API - getCategories()")
        return listOf(
            "Business",
            "Entertainment",
            "General",
            "Health",
            "Science",
            "Sports",
            "Technology"
        )
    }
}