package com.example.coffeeapp.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCart(cartItems: List<CartItem>) {
        val json = gson.toJson(cartItems)
        sharedPreferences.edit().putString("cart_items", json).apply()
    }

    fun loadCart(): List<CartItem> {
        val json = sharedPreferences.getString("cart_items", null)
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun savePurchaseHistory(purchaseHistory: List<List<CartItem>>, purchaseTimestamps: List<String>) {
        sharedPreferences.edit().apply {
            putString("purchase_history", gson.toJson(purchaseHistory))
            putString("purchase_timestamps", gson.toJson(purchaseTimestamps))
        }.apply()
    }

    fun loadPurchaseHistory(): Pair<List<List<CartItem>>, List<String>> {
        val historyJson = sharedPreferences.getString("purchase_history", null)
        val timestampsJson = sharedPreferences.getString("purchase_timestamps", null)
        val historyType = object : TypeToken<List<List<CartItem>>>() {}.type
        val timestampsType = object : TypeToken<List<String>>() {}.type
        val history = gson.fromJson<List<List<CartItem>>>(historyJson, historyType) ?: emptyList()
        val timestamps = gson.fromJson<List<String>>(timestampsJson, timestampsType) ?: emptyList()
        return Pair(history, timestamps)
    }
} 