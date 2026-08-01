package com.hdclark.nutritionguesser

import com.hdclark.nutritionguesser.data.FoodCatalog
import com.hdclark.nutritionguesser.data.FoodCategory
import com.hdclark.nutritionguesser.game.ComparisonDirection
import com.hdclark.nutritionguesser.game.FoodSide
import com.hdclark.nutritionguesser.game.GameEngine
import com.hdclark.nutritionguesser.game.GameState
import com.hdclark.nutritionguesser.game.GameStatus
import com.hdclark.nutritionguesser.game.NutrientMetric
import com.hdclark.nutritionguesser.game.QuestionFactory
import com.hdclark.nutritionguesser.game.QuestionProvider
import com.hdclark.nutritionguesser.game.QuizQuestion
import com.hdclark.nutritionguesser.game.valueFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameLogicTest {
    @Test
    fun wrongAnswerMovesQuestionToBack() {
        val engine = GameEngine(FixedQuestions())
        val original = requireNotNull(engine.state.currentQuestion)
        engine.answer(opposite(original.correctSide))
        assertEquals(1, engine.state.incorrectGuesses)
        assertNotEquals(original.id, engine.state.currentQuestion?.id)
        assertEquals(original.id, engine.state.questionQueue.last().id)
        assertEquals(2, engine.state.questionQueue.last().attempt)
    }

    @Test
    fun fiveWrongAnswersEndTheGame() {
        val engine = GameEngine(FixedQuestions())
        repeat(5) {
            val question = requireNotNull(engine.state.currentQuestion)
            engine.answer(opposite(question.correctSide))
        }
        assertEquals(GameStatus.GAME_OVER, engine.state.status)
        assertEquals(5, engine.state.incorrectGuesses)
    }

    @Test
    fun tenCorrectAnswersUnlockNextLevel() {
        val engine = GameEngine(FixedQuestions())
        repeat(10) {
            val question = requireNotNull(engine.state.currentQuestion)
            engine.answer(question.correctSide)
        }
        assertEquals(GameStatus.ROUND_COMPLETE, engine.state.status)
        engine.startNextRound()
        assertEquals(2, engine.state.level)
        assertEquals(GameStatus.PLAYING, engine.state.status)
    }

    @Test
    fun questionDifficultyProgressesAsDesigned() {
        val easy = QuestionFactory(random = Random(7)).generate(1, 10)
        assertTrue(easy.all { it.leftFood.group != it.rightFood.group })
        val hard = QuestionFactory(random = Random(11)).generate(6, 10)
        assertTrue(hard.all { it.leftFood.category == it.rightFood.category })
        hard.forEach {
            assertNotEquals(it.leftFood.valueFor(it.metric), it.rightFood.valueFor(it.metric), 0.0001)
        }
    }

    @Test
    fun catalogContainsRequiredVarietyAndValidValues() {
        val foods = FoodCatalog.foods
        assertTrue(foods.size >= 50)
        assertEquals(foods.size, foods.map { it.name }.distinct().size)
        listOf("Cheeseburger", "Hot dog", "Ramen noodles", "Macaroni and cheese", "Cheese omelette", "Nachos with cheese").forEach { required ->
            assertTrue(foods.any { it.name == required })
        }
        assertTrue(foods.any { it.category == FoodCategory.VEGETABLE })
        assertTrue(foods.any { it.category == FoodCategory.LEGUME })
        assertTrue(foods.all {
            it.calories >= 0 && it.proteinGrams >= 0 && it.saturatedFatGrams >= 0 &&
                it.transFatGrams >= 0 && it.fibreGrams >= 0 && it.sodiumMilligrams >= 0
        })
    }

    private fun opposite(side: FoodSide) = if (side == FoodSide.LEFT) FoodSide.RIGHT else FoodSide.LEFT

    private class FixedQuestions : QuestionProvider {
        private var generation = 0L
        override fun generate(level: Int, count: Int): List<QuizQuestion> {
            val apple = FoodCatalog.foods.first { it.name == "Apple" }
            val burger = FoodCatalog.foods.first { it.name == "Cheeseburger" }
            return List(count) { index ->
                QuizQuestion(
                    id = generation * 100 + index + 1L,
                    leftFood = apple,
                    rightFood = burger,
                    metric = NutrientMetric.CALORIES,
                    direction = ComparisonDirection.HIGHER,
                )
            }.also { generation++ }
        }
    }
}
