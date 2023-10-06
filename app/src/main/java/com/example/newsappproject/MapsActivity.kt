package com.example.newsappproject

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


private const val LOG_TAG = "MAPS PAGE"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationBtn : Button
    private lateinit var locationTextView: TextView
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var arrowIcon : ImageView

    private val defaultLat : Double = 38.898365   //GWU
    private val defaultLong: Double = -77.046753  //GWU

    private var defaultAnimationZoom = 10.0f


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
        arrowIcon = findViewById(R.id.locationButtonIcon)
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

        // Set initial map marker
        setMapMarker(null)

        // Map onLongClickListener
        mMap.setOnMapLongClickListener{mapOnLongClick(it)}
    }


    private fun mapOnLongClick(it: LatLng) {
        Log.d(LOG_TAG, "Long click at Lat: ${it.latitude}, Long:${it.longitude}.")
        setMapMarker(it)
    }


    private fun setMapMarker(location: LatLng?) {

        mMap.clear()

        val sharedPrefsMapsActivity = getSharedPreferences("MapsActivity", MODE_PRIVATE)

        val animationZoom =
            if (location == null) {
                defaultAnimationZoom
            } else {
                mMap.cameraPosition.zoom
            }

        val currentLatLng =
            if (location == null) {
                val lastUsedLat = sharedPrefsMapsActivity.getDouble("Lat", defaultLat)
                val lastUsedLong = sharedPrefsMapsActivity.getDouble("Long", defaultLong)
                LatLng(lastUsedLat, lastUsedLong)
            } else {
                location
            }

        val results = try {
            val geocoder = Geocoder(this, Locale.getDefault())
            geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 3)
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
            val currentAddress = results[0]
            val addressLine = currentAddress.getAddressLine(0)

            mMap.addMarker(MarkerOptions().position(currentLatLng).title(addressLine))?.showInfoWindow()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, animationZoom))
            locationTextView.text = addressLine

            // Button onClickListener
            locationBtn.setOnClickListener(btnOnClick(currentAddress))
            setButtonActive(true, currentAddress, null, null)

            // SharedPreferences
            sharedPrefsMapsActivity.edit().putDouble("Lat", currentLatLng.latitude).apply()
            sharedPrefsMapsActivity.edit().putDouble("Long", currentLatLng.longitude).apply()

            // Button onClickListener
            locationBtn.setOnClickListener(btnOnClick(currentAddress))
        }
    }


    private fun btnOnClick(currentAddress: Address): View.OnClickListener {
        return View.OnClickListener {
            Log.d(LOG_TAG, currentAddress.toString())

            val country : String = currentAddress.countryName
            val state : String ? = currentAddress.adminArea
            val query = state ?: country

            getArticles(query)

            setButtonActive(false, null, query, getString(R.string.viewing_news_for))
        }
    }


    private fun setButtonActive(active : Boolean, address : Address ?, query: String ?, disabledText : String ?) {

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
            locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            locationTextView.text = address!!.getAddressLine(0)
            arrowIcon.setColorFilter(grey, PorterDuff.Mode.SRC_IN)
        } else {
            locationBtn.isEnabled = false
            locationBtn.text = disabledText
            locationBtn.setTextColor(black)
            locationBtn.setBackgroundColor(white)
            locationTextView.setTextColor(black)
            locationTextView.setTypeface(null, Typeface.BOLD)
            locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            locationTextView.text = query
            arrowIcon.setColorFilter(white, PorterDuff.Mode.SRC_IN)
        }
    }


    private fun getArticles(query: String) {

        Log.d(LOG_TAG, "API - getSources($query)")

        // Coroutines
        CoroutineScope(Dispatchers.IO).launch {
            val articles = ApiManager(this@MapsActivity).getArticles(query, null)
            withContext(Dispatchers.Main) {
                if (articles.isEmpty()) {
                    setButtonActive(false, null, query, getString(R.string.no_news_for))
                    articlesRecyclerView.isVisible = false
                } else {
                    // RecyclerView
                    articlesRecyclerView.adapter = ArticlesAdapter(this@MapsActivity, articles)

                    articlesRecyclerView.onFlingListener = null
                    LinearSnapHelper().attachToRecyclerView(articlesRecyclerView)

                    articlesRecyclerView.layoutManager = LinearLayoutManager(this@MapsActivity, LinearLayoutManager.HORIZONTAL, false)
                    articlesRecyclerView.isVisible = true
                }
            }
        }
    }


    // Custom function to store Double in sharedPrefs
    private fun SharedPreferences.Editor.putDouble(key: String, double: Double) : SharedPreferences.Editor {
        return putLong(key, java.lang.Double.doubleToRawLongBits(double))
    }

    // Custom function to retrieve Double in sharedPrefs
    private fun SharedPreferences.getDouble(key: String, default: Double) : Double {
        return java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))
    }
}
