package com.example.newsappproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText

private const val LOG_TAG = "HOME PAGE"

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: Button
    private lateinit var topHeadlinesBtn: Button
    private lateinit var newsByLocationBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init
        searchEditText = findViewById(R.id.SearchEditText)
        searchBtn = findViewById(R.id.SearchButton)
        topHeadlinesBtn = findViewById(R.id.TopHeadlinesButton)
        newsByLocationBtn = findViewById(R.id.NewsByLocationButton)

        val sharedPrefsMainActivity = getSharedPreferences("MainActivity", MODE_PRIVATE)


        // Text Watchers
        searchEditText.addTextChangedListener(textWatcher)
        searchEditText.setText(sharedPrefsMainActivity.getString("searchTerm", ""))
        searchBtn.isEnabled = searchEditText.text.toString().isNotBlank()


        // Button onClickListeners
        searchBtn.setOnClickListener {
            Log.d(LOG_TAG, "BUTTON - Search button clicked")
            if (searchBtn.isEnabled) {
                val searchResultIntent = Intent(this@MainActivity, SourcesActivity::class.java)
                val searchTerm : String = searchEditText.text.toString()
                Log.d(LOG_TAG, "SEARCH - Search term: '$searchTerm'")
                sharedPrefsMainActivity.edit().putString("searchTerm", searchTerm).apply()
                searchResultIntent.putExtra("SearchTerm", searchTerm)
                startActivity(searchResultIntent)
            }
        }

        topHeadlinesBtn.setOnClickListener {
            Log.d(LOG_TAG, "BUTTON - Top Headlines button clicked")

        }

        newsByLocationBtn.setOnClickListener {
            Log.d(LOG_TAG, "BUTTON - News By Location button clicked")
            val mapsActivityIntent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(mapsActivityIntent)
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
