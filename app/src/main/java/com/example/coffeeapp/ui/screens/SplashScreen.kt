package com.example.coffeeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.coffeeapp.R
import com.example.coffeeapp.ui.SetStatusBarIconsLight
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavHostController){

    SetStatusBarIconsLight(isLightIcons = true)
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("Menu") {
            popUpTo("splash") { inclusive = true }
        }
    }
    Column(
        modifier = Modifier.background(color = Color.Black)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ){
            Image(
                painter = painterResource(id = R.drawable.splash_image),
                contentDescription = "background image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Coffee Shop",
                fontSize = 70.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 180.dp)
            )

            Spacer(modifier = Modifier.width(25.dp))

            Text(
                text = "Find the best coffee for you",
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 140.dp)
            )
        }
    }
}