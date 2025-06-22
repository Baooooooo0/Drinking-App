package com.example.coffeeapp.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat

@Composable
    fun SetStatusBarIconsLight(isLightIcons: Boolean) {
    val context = LocalContext.current
    val window = (context as Activity).window

    SideEffect {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !isLightIcons
    }
}
