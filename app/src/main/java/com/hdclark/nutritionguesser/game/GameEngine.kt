package com.hdclark.nutritionguesser.game

class GameEngine(
    private val questionProvider: QuestionProvider,
) {
    var state: GameState = newState(level = 1, correct = 0, incorrect = 0)
        private set

    fun answer(side: FoodSide): AnswerFeedback? {
        if (state.status != GameStatus.PLAYING) return null
        val question = state.currentQuestion ?: return null
        val chosenFood = if (side == FoodSide.LEFT) question.leftFood else question.rightFood
        val correctFood = if (question.correctSide == FoodSide.LEFT) question.leftFood else question.rightFood
        val wasCorrect = side == question.correctSide

        if (wasCorrect) {
            val remaining = state.questionQueue.drop(1)
            state = state.copy(
                correctGuesses = state.correctGuesses + 1,
                solvedInRound = state.solvedInRound + 1,
                questionQueue = remaining,
                status = if (remaining.isEmpty()) GameStatus.ROUND_COMPLETE else GameStatus.PLAYING,
            )
        } else {
            val incorrect = state.incorrectGuesses + 1
            if (incorrect >= GameState.MAX_INCORRECT_GUESSES) {
                state = state.copy(
                    incorrectGuesses = incorrect,
                    questionQueue = emptyList(),
                    status = GameStatus.GAME_OVER,
                )
            } else {
                val retry = question.copy(attempt = question.attempt + 1)
                state = state.copy(
                    incorrectGuesses = incorrect,
                    questionQueue = state.questionQueue.drop(1) + retry,
                )
            }
        }

        return AnswerFeedback(wasCorrect, chosenFood, correctFood, question.metric)
    }

    fun startNextRound() {
        if (state.status != GameStatus.ROUND_COMPLETE) return
        state = newState(state.level + 1, state.correctGuesses, state.incorrectGuesses)
    }

    fun restart() {
        state = newState(level = 1, correct = 0, incorrect = 0)
    }

    private fun newState(level: Int, correct: Int, incorrect: Int): GameState = GameState(
        level = level,
        correctGuesses = correct,
        incorrectGuesses = incorrect,
        solvedInRound = 0,
        questionQueue = questionProvider.generate(level, GameState.QUESTIONS_PER_ROUND),
        status = GameStatus.PLAYING,
    )
}
