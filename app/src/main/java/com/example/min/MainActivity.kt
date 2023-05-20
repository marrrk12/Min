package com.example.min

import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
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
import java.io.IOException
import java.sql.SQLException


class MainActivity : AppCompatActivity() {

    // Переменная для работы с БД
    private lateinit var mDBHelper: DatabaseHelper
    private lateinit var mDb: SQLiteDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var  countyofuser: TextView
    private lateinit var  street: TextView
    private lateinit var  listView: ListView
    private lateinit var img_of_county : ImageView
    private var arr_of_place : Array<String> =  arrayOf("undefined", "undefined", "undefined")

    private var address: Address? = Address("undefined",
        "undefined","undefined","undefined","undefined","undefined")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)

        countyofuser = findViewById(R.id.textView)
        //street = findViewById(R.id.textView2)
        img_of_county = findViewById(R.id.img_of_county)
        requestPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mDBHelper = DatabaseHelper(this)

        try {
            mDBHelper.updateDataBase()
        } catch (mIOException: IOException) {
            throw Error("UnableToUpdateDatabase")
        }

        mDb = try {
            mDBHelper.getWritableDatabase()
        } catch (mSQLException: SQLException) {
            throw mSQLException
        }

        //getLoc()



        //setPicture()


        lifecycleScope.launch(Dispatchers.Main) {
            getLoc()
        }
        listView.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, arr_of_place)

        val i = 0

    }

    private fun getLoc(){
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
            != PackageManager.PERMISSION_GRANTED )
        {
            return
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
            lifecycleScope.launch(Dispatchers.IO)
            {
                address = getPlace(it.latitude, it.longitude)
                launch(Dispatchers.Main)
                {
                    countyofuser.text ="Вы прибываете в: " + address?.county ?: "undefined"
                    if (countyofuser.text != "undefined")
                        setPicture()
                }
            }
        }
    }


    private fun setPicture() {
        var mDrawableName = ""
        var mNameId = ""
        var mNamecounty = ""
        val county = address?.county
        lifecycleScope.launch(Dispatchers.IO)
        {

            val cursor: Cursor = mDb.rawQuery("SELECT * FROM table_of_rngr WHERE name = '$county'", null)
            cursor.moveToFirst()

//            while (!cursor.isAfterLast) {
                mNamecounty = cursor.getString(0)
                mDrawableName = cursor.getString(1)

//                cursor.moveToNext()
//            }
            cursor.close()

            val i = 0


            val cursor2: Cursor = mDb.rawQuery("SELECT * FROM table_of_places WHERE id = (SELECT id_pl FROM table_of_rngr WHERE name = '" + county + "')", null)
            cursor2.moveToFirst()

            while (!cursor2.isAfterLast) {
                arr_of_place = arrayOf(cursor2.getString(1),cursor2.getString(2),cursor2.getString(3))
                cursor2.moveToNext()
            }
            cursor2.close()

            val resID = resources.getIdentifier(mDrawableName, "drawable", packageName)
            lifecycleScope.launch(Dispatchers.Main)
            {
                Picasso.get()
                    .load(resID)
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