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
        val url = "https://newsapi.org/v2/top-headlines/sources?apiKey=$apiKey&category=$category"
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

    fun getArticles(query : String, source : String ?) :List<Article> {
        var url = "https://newsapi.org/v2/everything?apiKey=$apiKey&searchIn=title&q=$query"

//        if (source != null) {
//            url = "$url&source=$source"
//        }

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        Log.d(LOG_TAG, "getArticles Response: $response")
        if (!response.isSuccessful) {
            Log.d(LOG_TAG, "getArticles UNSUCCESSFUL")
            return emptyList()
        } else {
            val responseBody : String ? = response.body?.string()
            val articlesJson = JSONObject(responseBody).getJSONArray("articles")

            val articles : MutableList<Article> = mutableListOf<Article>()

            for (i in 0 until articlesJson.length()){
                val currentArticleJson = articlesJson.getJSONObject(i)
                val title = currentArticleJson.getString("title")
                val articleSource = currentArticleJson.getJSONObject("source").getString("name")
                val description = currentArticleJson.getString("description")
                val thumbnailUrl = currentArticleJson.getString("urlToImage")
                val articleUrl = currentArticleJson.getString("url")

                val currentArticle = Article(title, articleSource, description, thumbnailUrl, articleUrl)

                articles.add(currentArticle)
            }
            return articles
        }
    }

}