package com.example.coffeeapp.model

data class DrinkData(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var extra: String = "",
    var price: Double = 0.0,
    var favourite: Boolean = false,
    var categoryId: String = "",
    var picUrl: List<String> = emptyList()
)


