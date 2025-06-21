package com.example.coffeeapp.model

data class CartItem(
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageRes: Int
)