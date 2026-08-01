package com.hdclark.nutritionguesser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hdclark.nutritionguesser.ui.NutritionGuesserApp
import com.hdclark.nutritionguesser.ui.theme.NutritionGuesserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutritionGuesserTheme {
                NutritionGuesserApp()
            }
        }
    }
}
