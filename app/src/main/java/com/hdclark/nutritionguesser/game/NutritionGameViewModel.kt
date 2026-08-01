package com.hdclark.nutritionguesser.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NutritionGameViewModel : ViewModel() {
    private val engine = GameEngine(QuestionFactory())

    var state by mutableStateOf(engine.state)
        private set

    var feedback by mutableStateOf<AnswerFeedback?>(null)
        private set

    fun answer(side: FoodSide) {
        feedback = engine.answer(side)
        state = engine.state
    }

    fun clearFeedback() {
        feedback = null
    }

    fun nextRound() {
        feedback = null
        engine.startNextRound()
        state = engine.state
    }

    fun restart() {
        feedback = null
        engine.restart()
        state = engine.state
    }
}
