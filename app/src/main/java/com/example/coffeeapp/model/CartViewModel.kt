package com.example.coffeeapp.model

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartViewModel(private val repository: CartRepository) : ViewModel() {
    var cartItems = mutableStateListOf<CartItem>()
        private set

    var purchaseHistory = mutableStateListOf<List<CartItem>>()
        private set

    var purchaseTimestamps = mutableStateListOf<String>()
        private set

    private val _totalPrice = mutableStateOf(0.0)
    val totalPrice get() = _totalPrice.value

    init {
        loadCart()
        loadPurchaseHistory()
    }

    fun addToCart(item: CartItem) {
        val existingIndex = cartItems.indexOfFirst { it.name == item.name }
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
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price
        }
        if (index != -1) {
            cartItems.removeAt(index)
            recalculateTotal()
            saveCart()
        }
    }

    fun increaseQuantity(item: CartItem) {
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price
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
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price
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
        val index = cartItems.indexOfFirst {
            it.name == item.name && it.price == item.price
        }
        if (index != -1) {
            cartItems[index] = cartItems[index].copy(quantity = newQuantity)
            recalculateTotal()
            saveCart()
        }
    }

    fun removeItem(index: Int) {
        if (index in cartItems.indices) {
            cartItems.removeAt(index)
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
    }

    private fun saveCart() {
        repository.saveCart(cartItems)
    }

    private fun loadCart() {
        val items = repository.loadCart()
        cartItems.clear()
        cartItems.addAll(items)
        recalculateTotal()
    }

    private fun savePurchaseHistory() {
        repository.savePurchaseHistory(purchaseHistory, purchaseTimestamps)
    }

    private fun loadPurchaseHistory() {
        val (history, timestamps) = repository.loadPurchaseHistory()
        purchaseHistory.clear()
        purchaseTimestamps.clear()
        purchaseHistory.addAll(history)
        purchaseTimestamps.addAll(timestamps)
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun clearPurchaseHistory() {
        purchaseHistory.clear()
        purchaseTimestamps.clear()
        savePurchaseHistory()
    }

    companion object {
        fun provideFactory(repository: CartRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return CartViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}

