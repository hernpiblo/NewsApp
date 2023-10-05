package com.example.newsappproject

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

private const val LOG_TAG = "API MANAGER"

class ApiManager(appContext : Context) {
    private val okHttpClient : OkHttpClient
    private val apiKey = appContext.getString(R.string.news_api_key)

    init {
        val builder = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    fun getSources(category : String) : List<Sources> {
        val url = "https://newsapi.org/v2/top-headlines/sources?category=$category&apiKey=$apiKey"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        Log.d(LOG_TAG, "getSources Response: $response")
        if (!response.isSuccessful) {
            Log.d(LOG_TAG, "getSources UNSUCCESSFUL")
            return emptyList()
        } else {
            val responseBody : String ? = response.body?.string()
            val sourcesJson = JSONObject(responseBody).getJSONArray("sources")

            val sources : MutableList<Sources> = mutableListOf<Sources>()

            for (i in 0 until sourcesJson.length()){
                val currentSourceJson = sourcesJson.getJSONObject(i)
                val name = currentSourceJson.getString("name")
                val description = currentSourceJson.getString("description")
                val currentSource = Sources(name, description)
                sources.add(currentSource)
            }
            return sources
        }
    }

    fun getArticles(query : String) :List<Article> {
        val url = "https://newsapi.org/v2/everything?apiKey=$apiKey"
        return emptyList()
    }

}