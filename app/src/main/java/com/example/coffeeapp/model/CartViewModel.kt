// Trong file app/src/main/java/com/example/coffeeapp/model/CartViewModel.kt
package com.example.coffeeapp.model

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartViewModel : ViewModel() {
    var cartItems = mutableStateListOf<CartItem>()
        private set

    var purchaseHistory = mutableStateListOf<List<CartItem>>()
        private set

    var purchaseTimestamps = mutableStateListOf<String>()
        private set

    private val _totalPrice = mutableStateOf(0.0)
    val totalPrice get() = _totalPrice.value

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
        loadCart()
        loadPurchaseHistory()
    }

    fun addToCart(item: CartItem) {
        // SỬA LẠI LOGIC KIỂM TRA: Thêm điều kiện `it.size == item.size`
        val existingIndex = cartItems.indexOfFirst { it.name == item.name && it.size == item.size }
        if (existingIndex != -1) {
            cartItems[existingIndex] = cartItems[existingIndex].copy(
                quantity = cartItems[existingIndex].quantity + 1
            )
        } else {
            cartItems.add(item.copy())
        }
        recalculateTotal()
        saveCart()
    }

    fun removeFromCart(item: CartItem) {
        // SỬA LẠI LOGIC TÌM KIẾM
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price && it.size == item.size
        }
        if (index != -1) {
            cartItems.removeAt(index)
            recalculateTotal()
            saveCart()
        }
    }

    fun increaseQuantity(item: CartItem) {
        // SỬA LẠI LOGIC TÌM KIẾM
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price && it.size == item.size
        }
        if (index != -1) {
            cartItems[index] = cartItems[index].copy(
                quantity = cartItems[index].quantity + 1
            )
            recalculateTotal()
            saveCart()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        // SỬA LẠI LOGIC TÌM KIẾM
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price && it.size == item.size
        }
        if (index != -1 && cartItems[index].quantity > 1) {
            cartItems[index] = cartItems[index].copy(
                quantity = cartItems[index].quantity - 1
            )
            recalculateTotal()
            saveCart()
        }
    }

    fun updateItemQuantity(item: CartItem, newQuantity: Int) {
        // SỬA LẠI LOGIC TÌM KIẾM
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price && it.size == item.size
        }
        if (index != -1) {
            cartItems[index] = cartItems[index].copy(quantity = newQuantity)
            recalculateTotal()
            saveCart()
        }
    }

    fun clearCart() {
        if (cartItems.isNotEmpty()) {
            val orderCopy = cartItems.map { it.copy() }
            purchaseHistory.add(orderCopy)

            val timestamp = getCurrentTimestamp()
            purchaseTimestamps.add(timestamp)

            savePurchaseHistory()
        }
        cartItems.clear()
        _totalPrice.value = 0.0
        saveCart()
    }

    private fun recalculateTotal() {
        _totalPrice.value = cartItems.sumOf { it.price * it.quantity }
        println("Debug - Recalculating total: ${_totalPrice.value}")
        cartItems.forEach { item ->
            println("Debug - Item: ${item.name}, Price: ${item.price}, Quantity: ${item.quantity}, Subtotal: ${item.price * item.quantity}")
        }
    }

    private fun saveCart() {
        if (::sharedPreferences.isInitialized) {
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(cartItems)
            editor.putString("cart_items", json)
            editor.apply()
        }
    }

    private fun loadCart() {
        if (::sharedPreferences.isInitialized) {
            val gson = Gson()
            val json = sharedPreferences.getString("cart_items", null)
            val type = object : TypeToken<List<CartItem>>() {}.type
            val items: List<CartItem>? = gson.fromJson(json, type)
            items?.let {
                cartItems.addAll(it)
                recalculateTotal()
            }
        }
    }

    private fun savePurchaseHistory() {
        if (::sharedPreferences.isInitialized) {
            val gson = Gson()
            sharedPreferences.edit().apply {
                putString("purchase_history", gson.toJson(purchaseHistory))
                putString("purchase_timestamps", gson.toJson(purchaseTimestamps))
            }.apply()
        }
    }

    private fun loadPurchaseHistory() {
        if (::sharedPreferences.isInitialized) {
            val gson = Gson()

            sharedPreferences.getString("purchase_history", null)?.let { json ->
                val type = object : TypeToken<List<List<CartItem>>>() {}.type
                val history = gson.fromJson<List<List<CartItem>>>(json, type)
                purchaseHistory.addAll(history)
            }

            sharedPreferences.getString("purchase_timestamps", null)?.let { json ->
                val type = object : TypeToken<List<String>>() {}.type
                val timestamps = gson.fromJson<List<String>>(json, type)
                purchaseTimestamps.addAll(timestamps)
            }
        }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun clearPurchaseHistory() {
        purchaseHistory.clear()
        purchaseTimestamps.clear()
    }
}