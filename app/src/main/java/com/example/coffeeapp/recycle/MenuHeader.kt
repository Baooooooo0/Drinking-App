package com.example.coffeeapp.recycle

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffeeapp.R
import com.example.coffeeapp.model.CategoryItemData

@Composable
fun HeaderMenu(categoryId: String, navController: NavController){
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, bottom = 25.dp, start = 12.dp, end = 12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            contentColor = Color.White,
            containerColor = Color.LightGray
        )
    ) {
        val categoryMap = mapOf(
            "1" to CategoryItemData("Ice Drink", R.drawable.ice_drink, "1"),
            "2" to CategoryItemData("Hot Drink", R.drawable.hot_drink, "2"),
            "3" to CategoryItemData("Hot Coffee", R.drawable.hot_coffee, "3"),
            "4" to CategoryItemData("Ice Coffee", R.drawable.ice_coffee, "4"),
            "5" to CategoryItemData("Brewing Coffee", R.drawable.brewing_coffee, "5"),
            "6" to CategoryItemData("Shake", R.drawable.shake, "6"),
            "7" to CategoryItemData("Take Away", R.drawable.restaurant, "7"),
            "8" to CategoryItemData("Breakfast", R.drawable.breakfast, "8"),
            "9" to CategoryItemData("Cake", R.drawable.cake, "9")
        )
        val selectedCategory = categoryMap[categoryId]
        selectedCategory?.let { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {navController.popBackStack()},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(60.dp)
                ) {
                    Text(
                        text = "<",
                        fontSize = 30.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(3f)
                ) {
                    Image(
                        painter = painterResource(id = category.iconResId),
                        contentDescription = category.label,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.label,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}