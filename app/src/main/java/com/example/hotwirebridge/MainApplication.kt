package com.example.hotwirebridge

import android.app.Application
import dev.hotwire.core.bridge.BridgeComponentFactory
import dev.hotwire.core.config.Hotwire
import dev.hotwire.navigation.config.registerBridgeComponents

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set configuration options
        Hotwire.registerBridgeComponents(
            BridgeComponentFactory("button", ::ButtonComponent)
        )
    }
}