package com.koroden.app7

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    private var locationManager: LocationManager? = null
    private var coordinates = ArrayList<Coordinates>()
    private lateinit var con: SQLiteDatabase;

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) { showInfo(location) }
        override fun onProviderDisabled(provider: String) { showInfo() }
        override fun onProviderEnabled(provider: String) { showInfo() }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) { showInfo() }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Проверяем есть ли разрешение
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
                PackageManager.PERMISSION_GRANTED) {
            // Разрешения нет. Нужно ли показать пользователю пояснения?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )) {
                    // Показываем пояснения
            }
            else {
                // Пояснений не требуется, запрашиваем разрешение
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
        else {
            // Разрешение есть, выполняем требуемое действие
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        val db = SQLiteHelper(this);
        con = db.readableDatabase
        getCoordinates()

        // Настройка плавающей кнопки
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddCoordinateActivity::class.java)
            startActivityForResult(intent, 0)
        }

        // Настройка списка
        val listView: ListView = findViewById(R.id.listCoordinates)
        listView.adapter = CoordinateAdapter(this, coordinates)

        listView.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val intent = Intent(this, AddCoordinateActivity::class.java)
            intent.putExtra("index", i)
            intent.putExtra("coordinates", coordinates[i])
            startActivityForResult(intent, 0)
        }

        listView.setOnItemLongClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->

            createAlertDialog(i, view1)
            return@setOnItemLongClickListener true
        }
    }

    private fun createAlertDialog(position: Int, view: View) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Удаление")
        builder.setMessage("Очистить элемент \"" + coordinates.get(position).placeName + "\"?")
        builder.setNegativeButton("Да") { dialog, which -> deleteItem(position, view) }
        builder.setPositiveButton("Нет") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun deleteItem(position: Int, view: View?) {
        val deletedStudent: Coordinates = coordinates.get(position)
        con.delete("coord", "id = ?", arrayOf(Integer.toString(deletedStudent.id)))
        coordinates.removeAt(position)

        val listView: ListView = findViewById(R.id.listCoordinates)
        (listView.adapter as CoordinateAdapter).notifyDataSetChanged()

        val snackBar = Snackbar.make(view!!, "Элемент очищен", Snackbar.LENGTH_LONG)
        snackBar.duration = 3000
        snackBar.show()
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
        {
            val index: Int = data?.getIntExtra("index", -1) ?: -1
            val coordinate: Coordinates = data?.getParcelableExtra("coordinates") ?: Coordinates()

            val cv = ContentValues()
            cv.put("place_name", coordinate.placeName)
            cv.put("latitude", coordinate.latitude)
            cv.put("longitude", coordinate.longitude)
            if (index != -1) {
                coordinates[index] = coordinate
                cv.put("id", coordinate.id)
                con.update("coord", cv, "id=?", arrayOf(coordinate.id.toString()))
            }
            else {
                coordinates.add(coordinate)
                con.insert("coord", null, cv)
            }

            val listView: ListView = findViewById(R.id.listCoordinates)
            (listView.adapter as CoordinateAdapter).notifyDataSetChanged()
        }
    }

    private fun getCoordinates() {
        val cursor = con.query(
            "coord", arrayOf("id", "place_name", "latitude", "longitude"),
            null, null, null, null, null
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val s = Coordinates()
            s.id = cursor.getInt(0)
            s.placeName = cursor.getString(1)
            s.latitude = cursor.getString(2)
            s.longitude = cursor.getString(3)
            coordinates.add(s)
            cursor.moveToNext()
        }
        cursor.close()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // Разрешение есть, заново выполняем требуемое действие
        }
        else {
            // Разрешения нет...
        }
    }

    private fun startTracking() {
        // Проверяем есть ли разрешение
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Здесь код работы с разрешениями...
        }
        else {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener)
            showInfo()
        }
    }

    private fun showInfo(location: Location? = null) {

        if (location != null) {
            if (location.provider == LocationManager.GPS_PROVIDER) {
                val a = 6378137.0
                val b = 6356752.3142
                val latitude = Math.toRadians(location.latitude)
                val longitude = Math.toRadians(location.longitude)

                val Nb = a * a / (sqrt(a * a * cos(latitude) * cos(latitude) + b * b * sin(latitude) * sin(latitude)))

                for (i in coordinates.indices) {
                    val x = Nb * cos(latitude) * cos(longitude)
                    val y = Nb * cos(latitude) * sin(longitude)

                    val xPlace = (Nb * cos(Math.toRadians(coordinates[i].latitude!!.toDouble())) * cos(Math.toRadians(coordinates[i].longitude!!.toDouble())))
                    val yPlace = (Nb * cos(Math.toRadians(coordinates[i].latitude!!.toDouble())) * sin(Math.toRadians(coordinates[i].longitude!!.toDouble())))
                    val radius = sqrt(abs(x - xPlace) * abs(x - xPlace) + abs(y - yPlace) * abs(y - yPlace))

                    if (radius < 100) {
                        coordinates[i].statusNumber = 1
                        coordinates[i].radius = radius.toInt()
                    } else {
                        coordinates[i].statusNumber = 0
                        coordinates[i].radius = radius.toInt()
                    }

                    val listView: ListView = findViewById(R.id.listCoordinates)
                    (listView.adapter as CoordinateAdapter).notifyDataSetChanged()
                }
            }
        }
    }

    private fun stopTracking() {
        locationManager!!.removeUpdates(locationListener)
    }

    override fun onResume() {
        super.onResume()
        startTracking()
    }

    override fun onPause() {
        super.onPause()
        stopTracking()
    }
}