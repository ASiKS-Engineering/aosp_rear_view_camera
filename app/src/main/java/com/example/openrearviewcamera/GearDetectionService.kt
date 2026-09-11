package com.example.openrearviewcamera

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager

class GearDetectionService : Service() {

    private val CHANNEL_ID = "GearDetectionChannel"
    private var car: Car? = null
    private var propertyManager: CarPropertyManager? = null

    private val gearPropertyListener = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            if (value.propertyId == VehiclePropertyIds.GEAR_SELECTION) {
                val gear = value.value as Int
                // GEAR_REVERSE is 4 in standard VHAL
                handleGearChange(gear == 4)
            }
        }

        override fun onErrorEvent(propId: Int, zones: Int) {
            Log.e("GearDetection", "CarProperty error: $propId")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE)
        
        initCarApi()
    }

    private fun initCarApi() {
        try {
            car = Car.createCar(this)
            propertyManager = car?.getCarManager(Car.PROPERTY_SERVICE) as? CarPropertyManager
            
            propertyManager?.registerCallback(
                gearPropertyListener,
                VehiclePropertyIds.GEAR_SELECTION,
                CarPropertyManager.SENSOR_RATE_ON_CHANGE
            )
        } catch (e: Exception) {
            Log.e("GearDetection", "Failed to connect to Car Service", e)
        }
    }

    private fun handleGearChange(isReverse: Boolean) {
        Log.d("GearDetection", "Reverse gear active: $isReverse")
        if (isReverse) {
            val intent = Intent(this, RearViewActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(intent)
        } else {
            val closeIntent = Intent("com.example.openrearviewcamera.CLOSE_REAR_VIEW").apply {
                setPackage(packageName)
            }
            sendBroadcast(closeIntent)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Gear Detection Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rear View Camera")
            .setContentText("Monitoring gear state...")
            .setSmallIcon(R.drawable.ic_menu_camera)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        propertyManager?.unregisterCallback(gearPropertyListener)
        car?.disconnect()
        super.onDestroy()
    }
}
