package com.example.coffeeapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeapp.model.CartRepository
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
    val context = LocalContext.current
    val cartRepository = remember(context) { CartRepository(context) }
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory(cartRepository))

    // DrinkListScreen()
    AppNavigation(cartViewModel)
}