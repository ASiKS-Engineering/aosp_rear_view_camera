package com.example.openrearviewcamera

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val seekBrightness = findViewById<SeekBar>(R.id.seekBrightness)
        val seekContrast = findViewById<SeekBar>(R.id.seekContrast)
        val spinnerFormat = findViewById<Spinner>(R.id.spinnerFormat)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val prefs = getSharedPreferences("camera_settings", MODE_PRIVATE)
        seekBrightness.progress = prefs.getInt("brightness", 50)
        seekContrast.progress = prefs.getInt("contrast", 50)
        spinnerFormat.setSelection(prefs.getInt("format_index", 0))

        btnSave.setOnClickListener {
            prefs.edit().apply {
                putInt("brightness", seekBrightness.progress)
                putInt("contrast", seekContrast.progress)
                putInt("format_index", spinnerFormat.selectedItemPosition)
                apply()
            }
            finish()
        }
    }
}
