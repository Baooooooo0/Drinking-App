package com.example.coffeeapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.coffeeapp.model.DrinkData
import com.example.coffeeapp.recycle.HeaderMenu
import com.example.coffeeapp.ui.SetStatusBarIconsLight
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Optimized color palette
private val BackgroundDark = Color(0xFF0D1117)
private val SurfaceDark = Color(0xFF161B22)
private val CardDark = Color(0xFF21262D)
private val TextPrimary = Color(0xFFF0F6FC)
private val TextSecondary = Color(0xFF8B949E)
private val TextSuccess = Color(0xFF3FB950)
private val AccentRed = Color(0xFFFF6B6B)
private val AccentRedDark = Color(0xFFDA3633)

@Composable
fun DrinkListScreen(categoryId: String, navController: NavController) {

    SetStatusBarIconsLight(isLightIcons = true)
    val drinks = remember { mutableStateListOf<DrinkData>() }

    LaunchedEffect(categoryId) {
        Log.d("DEBUG", "Loading drinks for categoryId: $categoryId")
        val database = FirebaseDatabase.getInstance(
            "https://coffeeappshoputh-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference

        database.child("Items")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val key = itemSnapshot.key ?: continue
                        val drink = itemSnapshot.getValue(DrinkData::class.java)
                        drink?.let {
                            it.id = key
                            it.favourite = itemSnapshot.child("favourite").getValue(Boolean::class.java) ?: false
                            if (it.categoryId == categoryId) {
                                drinks.add(it)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Load error: ${error.message}")
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundDark)
    ) {
        HeaderMenu(categoryId, navController = navController)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(drinks) { drink ->
                DrinkItemCard(
                    drink = drink,
                    onDrinkClick = { navController.navigate("drink_detail/${drink.id}") },
                    onFavouriteClick = {
                        val updatedDrink = drink.copy(favourite = !drink.favourite)
                        val index = drinks.indexOfFirst { it.id == drink.id }
                        if (index != -1) {
                            drinks[index] = updatedDrink
                        }
                        val database = FirebaseDatabase.getInstance(
                            "https://coffeeappshoputh-default-rtdb.asia-southeast1.firebasedatabase.app"
                        ).reference
                        database.child("Items").child(drink.id)
                            .child("favourite")
                            .setValue(updatedDrink.favourite)
                            .addOnSuccessListener {
                                Log.d("Firebase", "Favourite updated successfully")
                            }
                            .addOnFailureListener {
                                Log.e("Firebase", "Failed to update favourite: ${it.message}")
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun DrinkItemCard(
    drink: DrinkData,
    onDrinkClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onDrinkClick() },
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drink Image
                Card(
                    modifier = Modifier.size(108.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AsyncImage(
                        model = drink.picUrl.firstOrNull() ?: "",
                        contentDescription = "drink_image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Drink Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = drink.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (drink.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = drink.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Text(
                        text = "$${String.format("%.2f", drink.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSuccess
                    )
                }
            }

            // Favourite Icon
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clickable { onFavouriteClick() },
                colors = CardDefaults.cardColors(
                    containerColor = if (drink.favourite) AccentRed else CardDark
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (drink.favourite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (drink.favourite) Color.White else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
