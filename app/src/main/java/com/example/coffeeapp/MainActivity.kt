package com.example.coffeeapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeapp.model.CartViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppContent() {
    val cartViewModel: CartViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(context) {
        cartViewModel.init(context)
    }

    // DrinkListScreen()
    AppNavigation()
}