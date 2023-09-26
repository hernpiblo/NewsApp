package com.example.newsappproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: Button
    private lateinit var topHeadlinesBtn: Button
    private lateinit var newsByLocationBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.SearchEditText)
        searchBtn = findViewById(R.id.SearchButton)
        topHeadlinesBtn = findViewById(R.id.TopHeadlinesButton)
        newsByLocationBtn = findViewById(R.id.NewsByLocationButton)


        // Text Watchers
        searchEditText.addTextChangedListener(textWatcher)


        // Button onClickListeners
        searchBtn.setOnClickListener {
            Log.d("HomePage - Search button", "Search button clicked")
            if (searchBtn.isEnabled) {
                val searchResultIntent = Intent(this@MainActivity, FineTuneSearchActivity::class.java)
                val searchTerm : String = searchEditText.text.toString()
                Log.d("HomePage - Search button", "Search term: '$searchTerm'")
                searchResultIntent.putExtra("SearchTerm", searchTerm)
                startActivity(searchResultIntent)
            }
        }

        topHeadlinesBtn.setOnClickListener {
            Log.d("HomePage - Top Headlines button", "Top Headlines button clicked")

        }

        newsByLocationBtn.setOnClickListener {
            Log.d("HomePage - News By Location button", "News By Location button clicked")

        }


    }

    private val textWatcher : TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val searchTerm : String = searchEditText.text.toString()
            searchBtn.isEnabled = searchTerm.isNotBlank()
        }

        override fun afterTextChanged(p0: Editable?) {}
    }
}
