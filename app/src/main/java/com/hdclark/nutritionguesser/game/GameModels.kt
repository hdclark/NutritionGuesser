package com.hdclark.nutritionguesser.game

import com.hdclark.nutritionguesser.data.Food
import java.util.Locale
import kotlin.math.roundToInt

enum class NutrientMetric(
    val label: String,
    val unit: String,
    val minimumDifference: Double,
) {
    CALORIES("Calories", "kcal", 5.0),
    PROTEIN("Protein", "g", 0.3),
    SATURATED_FAT("Saturated fat", "g", 0.1),
    TRANS_FAT("Trans fat", "g", 0.05),
    FIBRE("Dietary fibre", "g", 0.2),
    SODIUM("Sodium", "mg", 10.0),
}

fun Food.valueFor(metric: NutrientMetric): Double = when (metric) {
    NutrientMetric.CALORIES -> calories
    NutrientMetric.PROTEIN -> proteinGrams
    NutrientMetric.SATURATED_FAT -> saturatedFatGrams
    NutrientMetric.TRANS_FAT -> transFatGrams
    NutrientMetric.FIBRE -> fibreGrams
    NutrientMetric.SODIUM -> sodiumMilligrams
}

fun NutrientMetric.format(value: Double): String = when (this) {
    NutrientMetric.CALORIES,
    NutrientMetric.SODIUM,
    -> "${value.roundToInt()} $unit"

    else -> String.format(Locale.US, "%.1f %s", value, unit)
}

enum class ComparisonDirection { HIGHER, LOWER }
enum class FoodSide { LEFT, RIGHT }

data class QuizQuestion(
    val id: Long,
    val leftFood: Food,
    val rightFood: Food,
    val metric: NutrientMetric,
    val direction: ComparisonDirection,
    val attempt: Int = 1,
) {
    val correctSide: FoodSide
        get() {
            val left = leftFood.valueFor(metric)
            val right = rightFood.valueFor(metric)
            return when (direction) {
                ComparisonDirection.HIGHER -> if (left > right) FoodSide.LEFT else FoodSide.RIGHT
                ComparisonDirection.LOWER -> if (left < right) FoodSide.LEFT else FoodSide.RIGHT
            }
        }

    val prompt: String
        get() = "Which has ${if (direction == ComparisonDirection.HIGHER) "more" else "less"} ${metric.label.lowercase()}?"
}

enum class GameStatus { PLAYING, ROUND_COMPLETE, GAME_OVER }

data class GameState(
    val level: Int,
    val correctGuesses: Int,
    val incorrectGuesses: Int,
    val solvedInRound: Int,
    val questionQueue: List<QuizQuestion>,
    val status: GameStatus,
) {
    val currentQuestion: QuizQuestion? get() = questionQueue.firstOrNull()
    val mistakesRemaining: Int get() = (MAX_INCORRECT_GUESSES - incorrectGuesses).coerceAtLeast(0)

    companion object {
        const val QUESTIONS_PER_ROUND = 10
        const val MAX_INCORRECT_GUESSES = 5
    }
}

data class AnswerFeedback(
    val wasCorrect: Boolean,
    val chosenFood: Food,
    val correctFood: Food,
    val metric: NutrientMetric,
) {
    val message: String
        get() {
            val correctValue = metric.format(correctFood.valueFor(metric))
            val chosenValue = metric.format(chosenFood.valueFor(metric))
            return if (wasCorrect) {
                "Nice! ${correctFood.name}: $correctValue"
            } else {
                "Not quite — ${correctFood.name}: $correctValue vs $chosenValue"
            }
        }
}
