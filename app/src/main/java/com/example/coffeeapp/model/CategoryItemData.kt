package com.example.coffeeapp.model

import androidx.annotation.DrawableRes

data class CategoryItemData(
    val label: String,
    @DrawableRes val iconResId: Int,
    val categoryId: String
)