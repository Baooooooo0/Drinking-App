@file:Suppress("UNREACHABLE_CODE")

package com.example.coffeeapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeeapp.ui.screens.CartScreen
import com.example.coffeeapp.ui.screens.DrinkDetailScreen
import com.example.coffeeapp.ui.screens.DrinkListScreen
import com.example.coffeeapp.ui.screens.FavouriteScreen
import com.example.coffeeapp.ui.screens.HistoryScreen
import com.example.coffeeapp.ui.screens.LoginScreen
import com.example.coffeeapp.ui.screens.MenuScreen
import com.example.coffeeapp.ui.screens.ProfileScreen
import com.example.coffeeapp.ui.screens.RegisterScreen
import com.example.coffeeapp.ui.screens.SplashScreen
import com.example.coffeeapp.model.CartViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()

    NavHost(navController = navController, startDestination = "Login") {
        composable("Login") { LoginScreen(navController) }
        composable("Register") { RegisterScreen(navController) }
        composable("Profile") { ProfileScreen(navController)}
        composable("Splash") { SplashScreen(navController)}
        composable("Menu") { MenuScreen(navController)}
        composable("Favourite") { FavouriteScreen(navController)}
        composable("Cart") { CartScreen(navController, cartViewModel) }
        composable("History") { HistoryScreen(navController, cartViewModel)}

        composable("category_items/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            DrinkListScreen(categoryId = categoryId, navController)
        }

        composable("drink_detail/{drinkId}") { backStackEntry ->
            val drinkId = backStackEntry.arguments?.getString("drinkId") ?: ""
            DrinkDetailScreen(drinkId = drinkId, navController = navController, cartViewModel = cartViewModel)
        }
    }
}
