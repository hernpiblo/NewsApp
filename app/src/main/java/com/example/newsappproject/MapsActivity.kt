package com.example.newsappproject

import android.graphics.Color
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappproject.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale


private const val LOG_TAG = "MAPS PAGE"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationBtn : Button
    private lateinit var locationTextView: TextView
    private lateinit var articlesRecyclerView: RecyclerView

    private val defaultLocation = LatLng(38.898365, -77.046753) // GWU
    private val defaultLocationTitle = "GWU"

    private var animationZoom = 10.0f
    private lateinit var currentAddress : Address


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Init
        supportActionBar?.title = "News by Location"

        locationBtn = findViewById(R.id.locationButton)
        locationTextView = findViewById(R.id.locationTextView)
        articlesRecyclerView = findViewById(R.id.articlesRecyclerView)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.addMarker(MarkerOptions().position(defaultLocation).title(defaultLocationTitle))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, animationZoom))

        val results = try {
            val geocoder = Geocoder(this, Locale.getDefault())
            geocoder.getFromLocation(defaultLocation.latitude, defaultLocation.longitude, 3)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Geocoding failed", e)
            listOf<Address>()
        }
        if (!results.isNullOrEmpty()) {
            currentAddress = results[0]
        }

        // Map onLongClickListener
        mMap.setOnMapLongClickListener{mapOnLongClick(it)}

        // Button onClickListener
        locationBtn.setOnClickListener(btnOnClick(currentAddress))
    }


    private fun mapOnLongClick(it: LatLng) {
        Log.d(LOG_TAG, "Long click at Lat: ${it.latitude}, Long:${it.longitude}.")

        mMap.clear()
        animationZoom = mMap.cameraPosition.zoom

        val results = try {
            val geocoder = Geocoder(this, Locale.getDefault())
            geocoder.getFromLocation(it.latitude, it.longitude, 3)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Geocoding failed", e)
            listOf<Address>()
        }

        if (results.isNullOrEmpty()) {
            Log.d(LOG_TAG, "No addresses found")

            AlertDialog.Builder(this)
                .setTitle("Unable to get address")
                .setMessage("Try another location")
                .setPositiveButton("OK", null)
                .show()
        } else {
            currentAddress = results[0]
            val addressLine = currentAddress.getAddressLine(0)
            mMap.addMarker(MarkerOptions().position(it).title(addressLine))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, animationZoom))
            locationTextView.text = addressLine

            // Button onClickListener
            locationBtn.setOnClickListener(btnOnClick(currentAddress))
            setButtonActive(true)
        }
    }


    private fun btnOnClick(currentAddress: Address): View.OnClickListener {
        return View.OnClickListener {
            Log.d(LOG_TAG, currentAddress.toString())
            val articles = getArticles(currentAddress)

            // RecyclerView
            articlesRecyclerView.adapter = ArticlesAdapter(this, articles)
            articlesRecyclerView.onFlingListener = null
            LinearSnapHelper().attachToRecyclerView(articlesRecyclerView)
            articlesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            articlesRecyclerView.isVisible = true
            setButtonActive(false)
        }
    }


    private fun setButtonActive(active : Boolean) {

        val black = Color.BLACK
        val white = Color.WHITE
        val grey = ContextCompat.getColor(this, android.R.color.secondary_text_dark)

        if (active) {
            locationBtn.isEnabled = true
            locationBtn.text = getString(R.string.show_news_for)
            locationBtn.setTextColor(grey)
            locationBtn.setBackgroundColor(black)
            locationTextView.setTextColor(white)
            locationTextView.setTypeface(null, Typeface.NORMAL)
        } else {
            locationBtn.isEnabled = false
            locationBtn.text = getString(R.string.viewing_news_for)
            locationBtn.setTextColor(black)
            locationBtn.setBackgroundColor(grey)
            locationTextView.setTextColor(black)
            locationTextView.setTypeface(null, Typeface.BOLD)
        }
    }


    private fun getArticles(currentAddress: Address) : List<Article> {

        val country : String = currentAddress.countryName
        val state : String ? = currentAddress.adminArea

        Log.d(LOG_TAG, "Get Articles: country: $country, state: $state.")

        val source = if (state.isNullOrBlank()) {
            "Reuters"
        } else {
            "BBC"
        }
        return listOf(
            Article("1 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak"),
            Article("2 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak"),
            Article("3 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak"),
            Article("4 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak"),
            Article("5 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak"),
            Article("6 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak"),
            Article("7 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak"),
            Article("8 Soccer mens tournament", source, "Soccer mens tournament aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddddd", "https://img-cdn.tnwcdn.com/image/tnw-blurple?filter_last=1&fit=1280%2C640&url=https%3A%2F%2Fcdn0.tnwcdn.com%2Fwp-content%2Fblogs.dir%2F1%2Ffiles%2F2023%2F10%2Fseergrills-BBQ.jpg&signature=cd73479302c0cbd19fb9a3c602aff91d","https://thenextweb.com/news/ai-powered-grill-cooks-food-up-to-10x-faster-perfect-steak")
        )
    }
}
