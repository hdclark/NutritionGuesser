package com.hdclark.nutritionguesser.data

enum class FoodGroup {
    NATURAL,
    PREPARED,
}

enum class FoodCategory {
    FAST_FOOD,
    PREPARED_MEAL,
    SNACK,
    DESSERT,
    BREAKFAST,
    FRUIT,
    VEGETABLE,
    LEGUME,
    NUT_SEED,
    GRAIN,
    DAIRY,
    PROTEIN,
}

data class Food(
    val name: String,
    val emoji: String,
    val group: FoodGroup,
    val category: FoodCategory,
    val calories: Double,
    val proteinGrams: Double,
    val saturatedFatGrams: Double,
    val transFatGrams: Double,
    val fibreGrams: Double,
    val sodiumMilligrams: Double,
)
