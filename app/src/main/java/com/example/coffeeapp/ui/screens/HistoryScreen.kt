package com.example.coffeeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffeeapp.model.CartItem
import com.example.coffeeapp.recycle.HeaderScreen
import com.example.coffeeapp.model.CartViewModel

// Optimized color palette
private val BackgroundDark = Color(0xFF0D1117)
private val SurfaceDark = Color(0xFF161B22)
private val CardDark = Color(0xFF21262D)
private val TextPrimary = Color(0xFFF0F6FC)
private val TextSecondary = Color(0xFF8B949E)
private val TextSuccess = Color(0xFF3FB950)
private val DangerRed = Color(0xFFDA3633)
private val AccentBlue = Color(0xFF58A6FF)
private val BorderSubtle = Color(0xFF30363D)

@Composable
fun HistoryScreen(navController: NavController, cartViewModel: CartViewModel) {
    val history = cartViewModel.purchaseHistory
    val timestamps = cartViewModel.purchaseTimestamps

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundDark)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        HeaderScreen(navController = navController, detail = "Purchase History")

        Spacer(modifier = Modifier.height(20.dp))

        if (history.isEmpty()) {
            EmptyHistoryState()
        } else {
            // Clear History Button
            Button(
                onClick = { cartViewModel.clearPurchaseHistory() },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear history",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Clear History",
                    fontWeight = FontWeight.SemiBold
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(history.reversed()) { index, order ->
                    val reversedIndex = history.size - 1 - index
                    val timestamp = timestamps.getOrNull(reversedIndex) ?: "Unknown time"
                    val orderTotal = order.sumOf { it.price * it.quantity }

                    OrderCard(
                        orderNumber = history.size - index,
                        timestamp = timestamp,
                        orderTotal = orderTotal,
                        items = order
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Sử dụng icon Receipt thay vì History
        Icon(
            imageVector = Icons.Default.Receipt,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Purchase History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Your completed orders will appear here",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OrderCard(
    orderNumber: Int,
    timestamp: String,
    orderTotal: Double,
    items: List<CartItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #$orderNumber",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = timestamp,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "$${String.format("%.2f", orderTotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSuccess
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items List
            items.forEach { item ->
                HistoryItemRow(item)
                if (item != items.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Qty: ${item.quantity}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$${String.format("%.2f", item.price)}/item",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$${String.format("%.2f", item.price * item.quantity)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextSuccess
            )
        }
    }
}
