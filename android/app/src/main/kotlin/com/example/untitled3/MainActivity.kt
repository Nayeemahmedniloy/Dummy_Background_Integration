// MainActivity.kt
package com.example.untitled3

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.untitled3.com.example.untitled3.PayHelper
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.untitled3/payment"
    private var yourPaymentService: YourPaymentService? = null
    private lateinit var payHelper: PayHelper

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as YourPaymentService.YourPaymentBinder
            yourPaymentService = binder.getService()
            Log.d("MainActivity", "Service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            yourPaymentService = null
            Log.d("MainActivity", "Service disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind to YourPaymentService
        Intent(this, YourPaymentService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // Ensure flutterEngine is initialized
        val messenger = flutterEngine?.dartExecutor?.binaryMessenger
        if (messenger != null) {
            payHelper = PayHelper(messenger)

            // Set up MethodChannel
            MethodChannel(messenger, CHANNEL).setMethodCallHandler { call, result ->
                if (call.method == "doTransaction") {
                    val functionId = call.argument<String>("functionId")
                    val amount = call.argument<String>("amount")
                    val appId = call.argument<String>("appId")

                    if (functionId != null && amount != null && appId != null) {
                        val transactionResult = payHelper.doTransaction(functionId, amount, appId)
                        result.success(transactionResult)
                    } else {
                        result.error("INVALID_ARGUMENTS", "Function ID, Amount, or App ID is null", null)
                    }
                } else {
                    result.notImplemented()
                }
            }
        } else {
            Log.e("MainActivity", "flutterEngine is null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}


