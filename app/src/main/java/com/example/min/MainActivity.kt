package com.example.min

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var  countyofuser: TextView
    private lateinit var  street: TextView
    private lateinit var  listView: ListView
    private  lateinit var img_of_county : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countyofuser = findViewById(R.id.textView)
        street = findViewById(R.id.textView2)
        img_of_county = findViewById(R.id.img_of_county)
        requestPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLoc()

        listView = findViewById(R.id.listView)
        listView.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("1", "2", "3"))

        setPicture()

    }

    private fun getLoc(){
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
            != PackageManager.PERMISSION_GRANTED )
        {
            return
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
            lifecycleScope.launch(Dispatchers.Main)
            {
//                countyofuser.text = it.latitude.toString()
//                street.text = it.longitude.toString()
                launch (Dispatchers.IO)
                {
                    val address = getPlace(it.latitude, it.longitude)
                    launch(Dispatchers.Main)
                    {
                        countyofuser.text = address?.county ?: address?.village ?: "NoFind"

                    }
                }
            }
        }
    }


    private fun setPicture() {

        lifecycleScope.launch(Dispatchers.IO)
        {
            val listImageUrl: ArrayList<Image> = ArrayList()
            val searchImageApi = RetrofitHelperImage.getInstance().create(SearchImageApi::class.java)
            val countyofuser1 = "Иглинский район"
            val result = searchImageApi.getPicture(countyofuser1.toString()+" карта")
            if (result != null && result.isSuccessful && result.body()?.items != null) {
                listImageUrl.addAll(result.body()!!.items)
            }

            lifecycleScope.launch(Dispatchers.Main)
            {
                Picasso.get()
                    .load(listImageUrl[0].link)
                    .into(img_of_county)
            }
        }
    }

    private suspend fun getPlace(late: Double, long: Double): Address? {
        val addressApi = RetrofitHelperSight.getInstance().create(AddressApi::class.java)
        val result = addressApi.getAddress(late, long)
        if (result != null)
            return result.body()?.address
        return null
    }


    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.INTERNET
            ), 1)
        }
    }
    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

}