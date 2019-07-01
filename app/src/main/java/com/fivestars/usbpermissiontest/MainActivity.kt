package com.fivestars.usbpermissiontest

import android.app.PendingIntent
import android.content.Context
import android.hardware.usb.UsbManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.content.BroadcastReceiver
import android.util.Log
import android.content.IntentFilter
import android.hardware.usb.UsbDevice


class MainActivity : AppCompatActivity() {

    private lateinit var device : UsbDevice

    private var txtBox: EditText? = null
    private var btnRefresh: Button? = null

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.i("Darran", "Got intent: " + action!!)
            device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

            val manager = getSystemService(Context.USB_SERVICE) as UsbManager
            manager.requestPermission(device, PendingIntent.getBroadcast(this@MainActivity, 0, Intent(7.toString()), 0))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtBox = findViewById<View>(R.id.editText1) as EditText
        btnRefresh = findViewById<View>(R.id.button1) as Button
        val permission = findViewById<View>(R.id.permission) as Button

        btnRefresh?.setOnClickListener(View.OnClickListener { refreshDevices() })

        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(br, filter)

        permission?.setOnClickListener(View.OnClickListener {
            val manager = getSystemService(Context.USB_SERVICE) as UsbManager
            manager.deviceList.forEach {
                manager.requestPermission(it.value, PendingIntent.getBroadcast(this@MainActivity, 0, Intent(7.toString()), 0))
            }
        })
    }

    private fun refreshDevices() {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        // Get the list of attached devices
        val devices = manager.deviceList

        txtBox?.setText("")
        txtBox?.setText("Number of devices: " + devices.size + "\n")

        // Iterate over all devices
        val it = devices.keys.iterator()
        while (it.hasNext()) {
            val deviceName = it.next()
            val device : UsbDevice = devices[deviceName]!!

            val VID = Integer.toHexString(device.vendorId).toUpperCase()
            val PID = Integer.toHexString(device.productId).toUpperCase()
            txtBox?.append(deviceName + " " + VID + ":" + PID + " " + manager.hasPermission(device) + "\n")
        }
    }
}
