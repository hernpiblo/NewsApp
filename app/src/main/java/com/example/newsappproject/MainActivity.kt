package com.example.newsappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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



    }
}