package com.koroden.app7

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast

class AddCoordinateActivity : AppCompatActivity() {
    private var index = 0
    private lateinit var coordinates: Coordinates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_coordinate)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        val intent = intent
        index = intent?.getIntExtra("index", -1) ?: -1
        coordinates = intent?.getParcelableExtra("coordinates") ?: Coordinates()

        findViewById<EditText>(R.id.place_name).setText(coordinates.placeName)
        findViewById<EditText>(R.id.latitude).setText(coordinates.latitude)
        findViewById<EditText>(R.id.longitude).setText(coordinates.longitude)

        // Включаем кнопку Назад
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_coordinate, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        if (item.itemId == R.id.action_save) {

            //Сохранение данных
            if(findViewById<EditText>(R.id.place_name).text.toString() != "" &&
                    findViewById<EditText>(R.id.latitude).text.toString() != "" &&
                    findViewById<EditText>(R.id.longitude).text.toString() != ""){

                this.coordinates.placeName = findViewById<EditText>(R.id.place_name).text.toString()
                this.coordinates.latitude = findViewById<EditText>(R.id.latitude).text.toString()
                this.coordinates.longitude = findViewById<EditText>(R.id.longitude).text.toString()

                val intent = Intent()
                intent.putExtra("index", index)
                intent.putExtra("coordinates", this.coordinates)
                setResult(Activity.RESULT_OK, intent)
            }else{
                val toast =
                        Toast.makeText(applicationContext, "Данные не были указаны", Toast.LENGTH_SHORT)
                toast.show()
            }

            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}