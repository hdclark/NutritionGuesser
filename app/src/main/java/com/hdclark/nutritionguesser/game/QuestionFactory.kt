package com.hdclark.nutritionguesser.game

import com.hdclark.nutritionguesser.data.Food
import com.hdclark.nutritionguesser.data.FoodCatalog
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

interface QuestionProvider {
    fun generate(level: Int, count: Int): List<QuizQuestion>
}

class QuestionFactory(
    private val foods: List<Food> = FoodCatalog.foods,
    private val random: Random = Random.Default,
) : QuestionProvider {
    private var nextId = 1L

    override fun generate(level: Int, count: Int): List<QuizQuestion> {
        require(count > 0) { "count must be positive" }
        val allCandidates = buildCandidates()
        val preferred = allCandidates.filter { it.matchesLevel(level) }
        val source = when {
            preferred.size >= count && level >= 5 -> preferred.sortedBy { it.relativeDifference }.take(max(count * 10, 50))
            preferred.size >= count -> preferred
            else -> allCandidates
        }
        val selected = source.shuffled(random)
            .distinctBy { candidate ->
                listOf(candidate.first.name, candidate.second.name).sorted().joinToString("|") + "|${candidate.metric}"
            }
            .take(count)
        check(selected.size == count) { "Not enough valid nutrition comparisons" }
        return selected.map { candidate ->
            val swap = random.nextBoolean()
            QuizQuestion(
                id = nextId++,
                leftFood = if (swap) candidate.second else candidate.first,
                rightFood = if (swap) candidate.first else candidate.second,
                metric = candidate.metric,
                direction = if (random.nextBoolean()) ComparisonDirection.HIGHER else ComparisonDirection.LOWER,
            )
        }
    }

    private fun buildCandidates(): List<Candidate> = buildList {
        for (firstIndex in 0 until foods.lastIndex) {
            for (secondIndex in firstIndex + 1 until foods.size) {
                val first = foods[firstIndex]
                val second = foods[secondIndex]
                for (metric in NutrientMetric.entries) {
                    val firstValue = first.valueFor(metric)
                    val secondValue = second.valueFor(metric)
                    val difference = abs(firstValue - secondValue)
                    if (difference < metric.minimumDifference) continue
                    if (metric == NutrientMetric.TRANS_FAT && firstValue == 0.0 && secondValue == 0.0) continue
                    add(
                        Candidate(
                            first = first,
                            second = second,
                            metric = metric,
                            relativeDifference = difference / max(max(firstValue, secondValue), metric.minimumDifference),
                        ),
                    )
                }
            }
        }
    }

    private fun Candidate.matchesLevel(level: Int): Boolean = when {
        level <= 1 -> first.group != second.group && relativeDifference >= 0.35
        level == 2 -> first.category != second.category && relativeDifference >= 0.22
        level == 3 -> first.group == second.group && first.category != second.category && relativeDifference >= 0.12
        level == 4 -> first.category == second.category && relativeDifference >= 0.08
        else -> first.category == second.category && relativeDifference in 0.04..0.45
    }

    private data class Candidate(
        val first: Food,
        val second: Food,
        val metric: NutrientMetric,
        val relativeDifference: Double,
    )
}
