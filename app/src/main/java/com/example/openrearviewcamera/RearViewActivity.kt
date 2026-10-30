package com.example.openrearviewcamera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalLensFacing
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RearViewActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView

    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rear_view)

        viewFinder = findViewById(R.id.viewFinder)
        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<Button>(R.id.btnExit).setOnClickListener {
            finish()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()

        ContextCompat.registerReceiver(
            this,
            closeReceiver,
            IntentFilter("com.example.openrearviewcamera.CLOSE_REAR_VIEW"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    @OptIn(ExperimentalLensFacing::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = try {
                cameraProviderFuture.get()
            } catch (exc: Exception) {
                Log.e(TAG, "Camera provider initialization failed", exc)
                showStatus(getString(R.string.no_camera_available))
                return@addListener
            }

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Prefer the external USB camera, then back, then any available camera
            val candidates = listOf(
                CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_EXTERNAL)
                    .build(),
                CameraSelector.DEFAULT_BACK_CAMERA,
                CameraSelector.DEFAULT_FRONT_CAMERA
            )
            val cameraSelector = candidates.firstOrNull { cameraProvider.hasCamera(it) }
            if (cameraSelector == null) {
                Log.e(TAG, "No camera available")
                showStatus(getString(R.string.no_camera_available))
                return@addListener
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                showStatus(getString(R.string.no_camera_available))
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun showStatus(message: String) {
        findViewById<TextView>(R.id.statusText).apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private companion object {
        const val TAG = "RearView"
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        unregisterReceiver(closeReceiver)
    }
}
