package com.example.coffeeapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.coffeeapp.R
import com.example.coffeeapp.recycle.FooterMenu
import com.example.coffeeapp.ui.SetStatusBarIconsLight
import com.google.firebase.auth.FirebaseUser

@Composable
fun MenuScreen(navHostController: NavHostController, user: FirebaseUser?) {
    SetStatusBarIconsLight(isLightIcons = true)
    val welcomeText = if (user?.displayName.isNullOrBlank()) {
        "Welcome!"
    } else {
        "Welcome\n${user.displayName}"
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.menu_bg),
            contentDescription = "background image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = welcomeText,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 64.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            CategoryPanel(navController = navHostController)

            Spacer(modifier = Modifier.height(65.dp))
        }
        FooterMenu(navController = navHostController)
    }
}